package co.com.pragma.solicitudes.sqs;

import co.com.pragma.solicitudes.model.applicationdecisionevent.ApplicationDecisionEvent;
import co.com.pragma.solicitudes.model.applicationdecisionevent.gateways.DecisionPublisher;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;

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

/**
 * Publicador de eventos en Amazon SQS.
 * Implementa el puerto DecisionPublisher definido en el dominio.
 * Usa AWS SDK v2 (cliente asíncrono) para enviar mensajes en formato JSON.
 */
@Slf4j // Habilita logging usando Lombok.
@Component // Spring lo detecta como un bean gestionado.
@RequiredArgsConstructor // Genera constructor con todos los atributos finales.
public class DecisionSqsPublisher implements DecisionPublisher {

    // Jackson para serializar los eventos a JSON antes de enviarlos a SQS.
    private final ObjectMapper objectMapper;

    // Región de AWS donde se encuentra la cola. Se inyecta desde application.yml.
    @Value("${aws.region}")
    private String awsRegion;

    // URL completa de la cola SQS (NO ARN). Se inyecta desde application.yml.
    @Value("${aws.sqs.decision-queue-url}")
    private String decisionQueueUrl;

    // Indica si la cola es FIFO. Se usa para configurar deduplicación y grupos.
    @Value("${aws.sqs.fifo:false}")
    private boolean fifo;

    // Clave pública de AWS. Se inyecta desde application.yml.
    @Value("${aws.credentials.access-key}")
    private String accessKey;

    // Clave privada de AWS. También se inyecta desde application.yml.
    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    // --- NUEVO: Cola de REPORTES ---
    @Value("${aws.sqs.reports-queue-url}")   private String reportsQueueUrl;
    @Value("${aws.sqs.reports-fifo:false}")  private boolean reportsFifo;

    /**
     * Crea una instancia del cliente asíncrono de Amazon SQS.
     * Usa la región y credenciales configuradas en el application.yml.
     * Podríamos extraerlo a un @Bean para reutilizarlo y optimizar recursos.
     */
    /** Crea cliente SQS con región/credenciales de application.yml */

    private SqsAsyncClient client() {
        return SqsAsyncClient.builder()
                .region(Region.of(awsRegion)) // Especificamos la región de AWS.
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey, // Access Key
                                        secretKey  // Secret Key
                                )
                        )
                )
                .build(); // Construimos el cliente SQS.
    }

    /**
     * Publica un evento de decisión en la cola SQS.
     * @param event Evento que contiene los datos de la decisión.
     * @return Mono<Void> que completa al terminar la publicación.
     */
    // =========================
    // (1) Publicación de DECISIÓN (lo que ya expones por el puerto DecisionPublisher)
    // =========================
    @Override
    public Mono<Void> publish(ApplicationDecisionEvent event) {
        // Logueamos la configuración actual de SQS para depuración.
        log.info("SQS config: region={}, url={}, fifo={}", awsRegion, decisionQueueUrl, fifo);

        // Detectamos si la URL apunta a una cola FIFO.
        boolean urlIsFifo = decisionQueueUrl != null && decisionQueueUrl.endsWith(".fifo");

        // Validamos que la configuración de FIFO coincida con la URL.
        if (urlIsFifo != fifo) {
            String msg = "Inconsistencia SQS FIFO: url=" + decisionQueueUrl + " fifo=" + fifo;
            log.error(msg);
            // Si hay inconsistencia, lanzamos un error y no publicamos nada.
            return Mono.error(new IllegalStateException(msg));
        }

        // Convertimos el evento a JSON de forma reactiva.
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))

                .flatMap(body -> {
                    // Construimos la solicitud para enviar el mensaje.
                    SendMessageRequest.Builder builder = SendMessageRequest.builder()
                            .queueUrl(decisionQueueUrl)  // Cola destino.
                            .messageBody(body);          // Payload JSON.

                    // Si la cola es FIFO, configuramos deduplicación y grupo de mensajes.
                    if (fifo) {
                        builder.messageGroupId("application-" + event.getIdApplication()) // Agrupamos por aplicación.
                                .messageDeduplicationId(event.getEventId()); // Evitamos duplicados usando eventId.
                    }

                    // Logueamos la publicación.
                    log.info(ApplicationConstants.LOG_SQS_PUBLISHING,
                            event.getIdApplication(), event.getDecision(), event.getCorrelationId());

                    // Enviamos el mensaje a SQS de forma asíncrona.
                    return Mono.fromFuture(client().sendMessage(builder.build()))
                            // Si es exitoso, logueamos el ID de mensaje devuelto por SQS.
                            .doOnNext(resp -> log.info(ApplicationConstants.LOG_SQS_PUBLISHED,
                                    event.getIdApplication(), resp.messageId()))
                            // Retornamos un Mono<Void> ya que no necesitamos devolver datos.
                            .then();
                })

                // Manejo de errores durante la serialización o envío.
                .onErrorResume(ex -> {
                    // Logueamos el error con detalles del evento y correlación.
                    log.error(ApplicationConstants.LOG_SQS_PUBLISH_ERROR,
                            event.getIdApplication(), event.getCorrelationId(), ex.toString());

                    // Propagamos el error para que la capa superior pueda manejarlo.
                    return Mono.error(ex);
                });
    }

    // =========================
    // (2) NUEVO: Publicación para REPORTES cuando queda APROBADA
    // =========================
    /**
     * Publica el evento mínimo que el micro REPORTES espera para contar una aprobación.
     * Este mensaje va a la COLA DE REPORTES, separada de la de decisiones/notificaciones.
     */
    public Mono<Void> publishApprovedForReports(String loanId, String email, Double amount, Integer term) {
        log.info("SQS reports config: region={} url={} fifo={}", awsRegion, reportsQueueUrl, reportsFifo);

        boolean urlIsFifo = reportsQueueUrl != null && reportsQueueUrl.endsWith(".fifo");
        if (urlIsFifo != reportsFifo) {
            String msg = "Inconsistencia SQS FIFO (reports): url=" + reportsQueueUrl + " fifo=" + reportsFifo;
            log.error(msg);
            return Mono.error(new IllegalStateException(msg));
        }

        // Construimos el payload EXACTO que consume el micro REPORTES:
        ApprovedEvent payload = new ApprovedEvent(loanId, email, amount, term);

        return Mono.fromCallable(() -> objectMapper.writeValueAsString(payload))
                .flatMap(body -> {
                    SendMessageRequest.Builder builder = SendMessageRequest.builder()
                            .queueUrl(reportsQueueUrl)
                            .messageBody(body);

                    if (reportsFifo) {
                        // Para REPORTES, la idempotencia la maneja Dynamo, pero si es FIFO mejor usar loanId
                        builder.messageGroupId("reports-" + loanId)
                                .messageDeduplicationId(loanId);
                    }

                    log.info("Publicando evento REPORTES: loanId={} email={} monto={} plazoMeses={}",
                            loanId, email, amount, term);

                    return Mono.fromFuture(client().sendMessage(builder.build()))
                            .doOnNext(resp -> log.info("Evento REPORTES publicado: loanId={} messageId={}",
                                    loanId, resp.messageId()))
                            .then();
                })
                .onErrorResume(ex -> {
                    log.error("Error publicando evento REPORTES (loanId={}): {}", loanId, ex.toString());
                    return Mono.error(ex);
                });
    }

    /** Record local con el contrato que espera REPORTES */
    record ApprovedEvent(String loanId, String email, Double amount, Integer term) {}
}