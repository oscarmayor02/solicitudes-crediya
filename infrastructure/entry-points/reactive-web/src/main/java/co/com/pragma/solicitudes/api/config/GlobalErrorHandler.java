package co.com.pragma.solicitudes.api.config;

import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Object body;

        if (ex instanceof ValidationException ve) {
            body = Map.of(
                    "code", "VALIDATION_ERROR",
                    "errors", List.of(ve.getMessage())
            );

        } else if (ex instanceof DomainExceptions de) {
            body = Map.of(
                    "code", de.getCode(),
                    "message", de.getMessage()
            );

        } else {
            log.error("Error no controlado", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body = Map.of(
                    "code", "INTERNAL_ERROR",
                    "message", "Ha ocurrido un error inesperado"
            );
        }

        try {
            String json = objectMapper.writeValueAsString(body);
            var response = exchange.getResponse();
            response.setStatusCode(status);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))
            ));
        } catch (Exception e) {
            log.error("Error serializando JSON de error", e);
            return Mono.empty();
        }
    }
}