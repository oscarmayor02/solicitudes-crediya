package co.com.pragma.solicitudes.sqs;


import co.com.pragma.solicitudes.model.reportevent.gateways.ReportsPublisher;
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

import java.math.BigDecimal;

/**
 * Adaptador SQS que envía el evento mínimo que consume el micro REPORTES:
 *   { loanId, email, amount, term }
 * Tu cola es STANDARD, por lo que no seteamos MessageGroupId/DedupId.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportsSqsPublisher implements ReportsPublisher {

    private final ObjectMapper objectMapper;

    // Región y credenciales (para local/envs simples). En prod usa roles.
    @Value("${aws.region}")                 private String awsRegion;
    @Value("${aws.credentials.access-key}") private String accessKey;
    @Value("${aws.credentials.secret-key}") private String secretKey;

    // URL de la cola de REPORTES (Standard)
    @Value("${aws.sqs.reports-queue-url}")  private String reportsQueueUrl;

    private SqsAsyncClient client() {
        return SqsAsyncClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Override
    public Mono<Void> publishApproved(String loanId, String email, BigDecimal amount, Integer term) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(
                        new Payload(loanId, email, amount, term)))
                .flatMap(body -> {
                    var req = SendMessageRequest.builder()
                            .queueUrl(reportsQueueUrl)
                            .messageBody(body)
                            .build();

                    log.info("Publicando a REPORTES cola={} loanId={} email={} amount={} term={}",
                            reportsQueueUrl, loanId, email, amount, term);

                    return Mono.fromFuture(client().sendMessage(req))
                            .doOnNext(resp -> log.info("REPORTES messageId={}", resp.messageId()))
                            .then();
                })
                .onErrorResume(e -> {
                    log.error("Error publicando a REPORTES loanId={} -> {}", loanId, e.toString(), e);
                    return Mono.error(e);
                });
    }

    /** Contrato que consume el micro REPORTES */
    record Payload(String loanId, String email, BigDecimal amount, Integer term) {}
}