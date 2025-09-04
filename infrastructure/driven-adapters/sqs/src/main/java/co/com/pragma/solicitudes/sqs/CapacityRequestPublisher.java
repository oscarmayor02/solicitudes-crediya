package co.com.pragma.solicitudes.sqs;

import co.com.pragma.solicitudes.model.capacity.CapacityRequestEvent;
import co.com.pragma.solicitudes.model.capacity.gateways.ValidationPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacityRequestPublisher implements ValidationPublisher {

    private final ObjectMapper objectMapper;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.sqs.capacity-req-url}")
    private String capacityReqQueueUrl;

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    private SqsAsyncClient client() {
        return SqsAsyncClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Override
    public Mono<Void> publish(CapacityRequestEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(body -> {
                    log.info("SQS-REQ: Enviando validación idApp={} bytes={} queue={}",
                            event.getIdApplication(), body.length(), capacityReqQueueUrl);
                    return Mono.fromFuture(client().sendMessage(
                            SendMessageRequest.builder()
                                    .queueUrl(capacityReqQueueUrl)
                                    .messageBody(body)
                                    .build()));
                })
                .doOnNext(resp -> log.info("SQS-REQ: messageId={}", resp.messageId()))
                .doOnError(err -> log.error("SQS-REQ ERROR idApp={}: {}", event.getIdApplication(), err.toString()))
                .then();
    }
}
