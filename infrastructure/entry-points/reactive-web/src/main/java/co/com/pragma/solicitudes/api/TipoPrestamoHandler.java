package co.com.pragma.solicitudes.api;
import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.usecase.tipoprestamo.TipoPrestamoUseCase;
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
public class TipoPrestamoHandler {
    private final TipoPrestamoUseCase useCase;
    private static final Logger log = LoggerFactory.getLogger(TipoPrestamoHandler.class);

    public static final String RUTA_TIPO_PRESTAMO = "/api/v1/tipos-prestamo";

    public Mono<ServerResponse> crear(ServerRequest request) {
        log.info("Solicitud para crear TipoPrestamo");
        return request.bodyToMono(TipoPrestamo.class)
                .doOnNext(tp -> log.debug("Cuerpo recibido: {}", tp))
                .flatMap(useCase::crear)
                .flatMap(saved -> {
                    log.info("TipoPrestamo creado: {}", saved);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(saved);
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Error al crear TipoPrestamo: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse(e.getMessage()));
                });
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        log.info("Solicitud para listar TiposPrestamo");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.listar().doOnNext(tp -> log.debug("TipoPrestamo listado: {}", tp)), TipoPrestamo.class);
    }

    public Mono<ServerResponse> obtenerPorId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info("Solicitud para obtener TipoPrestamo con id={}", id);

        return useCase.obtenerPorId(id)
                .flatMap(tp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tp))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.error("Error al obtener TipoPrestamo id={}: {}", id, e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse(e.getMessage()));
                });
    }

    private record ErrorResponse(String message) {}
}
