package co.com.pragma.solicitudes.api;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.usecase.loanType.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
@RequiredArgsConstructor
public class LoanTypeHandler {
    private final LoanTypeUseCase useCase;
    private static final Logger log = LoggerFactory.getLogger(LoanTypeHandler.class);

    public static final String RUTA_TIPO_PRESTAMO = "/api/v1/tipos-prestamo";

    public Mono<ServerResponse> createLoanType(ServerRequest request) {
        log.info("Application para crear LoanType");
        return request.bodyToMono(LoanType.class)
                .doOnNext(tp -> log.debug("Cuerpo recibido: {}", tp))
                .flatMap(useCase::create)
                .flatMap(saved -> {
                    log.info("LoanType creado: {}", saved);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(saved);
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Error al crear LoanType: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse(e.getMessage()));
                });
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        log.info("Application para listar TiposPrestamo");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.listLoanType().doOnNext(tp -> log.debug("LoanType listado: {}", tp)), LoanType.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info("Application para obtener LoanType con id={}", id);

        return useCase.getById(id)
                .flatMap(tp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tp))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Error al obtener LoanType id={}: {}", id, e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse(e.getMessage()));
                });
    }

    private record ErrorResponse(String message) {}
}
