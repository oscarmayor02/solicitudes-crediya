package co.com.pragma.solicitudes.sqs;

import co.com.pragma.solicitudes.model.capacity.CapacityResultEvent;
import co.com.pragma.solicitudes.usecase.application.ApplicationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacityResultConsumer {

    private final ObjectMapper objectMapper;
    private final ApplicationUseCase applicationUseCase;

    @Value("${aws.sqs.capacity-res-url}")
    private String capacityResQueueUrl;

    private final SqsAsyncClient client = SqsAsyncClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        client.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(capacityResQueueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(5)
                        .build())
                .thenAccept(resp -> resp.messages().forEach(msg -> {
                    try {
                        CapacityResultEvent event =
                                objectMapper.readValue(msg.body(), CapacityResultEvent.class);

                        process(event, msg.receiptHandle())
                                .doOnError(err -> log.error("Error procesando evento idApp={} err={}",
                                        event.getIdApplication(), err.getMessage()))
                                .subscribe();

                    } catch (Exception e) {
                        log.error("Error parseando mensaje SQS", e);
                        // Si el mensaje es inválido, elimínalo para no atascar la cola
                        client.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(capacityResQueueUrl)
                                .receiptHandle(msg.receiptHandle())
                                .build());
                    }
                }));
    }

    private Mono<Void> process(CapacityResultEvent event, String receiptHandle) {
        return applicationUseCase.applyAutoDecision(event)
                .doOnSuccess(app -> log.info("Actualizada solicitud {} a {}",
                        app.getIdApplication(), event.getDecision()))
                .flatMap(app -> Mono.fromFuture(client.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(capacityResQueueUrl)
                        .receiptHandle(receiptHandle)
                        .build())))
                .onErrorResume(err -> {
                    // Si ya estaba final o hubo error de validación, solo logueamos y NO borramos el mensaje
                    log.warn("Ignorando respuesta automática idApp={} motivo={}",
                            event.getIdApplication(), err.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
