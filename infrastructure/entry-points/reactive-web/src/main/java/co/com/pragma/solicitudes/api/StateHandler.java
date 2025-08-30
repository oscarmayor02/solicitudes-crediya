package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.usecase.state.StateUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component // Marca la clase como componente Spring
@RequiredArgsConstructor // Genera constructor con parámetros finales
public class StateHandler {

    private final StateUseCase useCase; // Caso de uso para manejar la lógica de negocio
    private static final Logger log = LoggerFactory.getLogger(StateHandler.class); // Logger para trazabilidad

    // Constantes para rutas
    public static final String RUTA_ESTADO = "/api/v1/estado";

    /**
     * Crear nuevo estado
     * POST /api/v1/estado
     */
    public Mono<ServerResponse> createState(ServerRequest request) {
        log.info("Application recibida para crear State"); // Log de trazabilidad

        return request.bodyToMono(State.class) // Convertimos el body JSON a objeto State
                .doOnNext(e -> log.debug("Cuerpo recibido: {}", e))
                .flatMap(useCase::createState) // Llamada al caso de uso
                .flatMap(saved -> {
                    log.info("State creado con éxito: {}", saved);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(saved);
                });
    }

    /**
     * Listar todos los estados
     * GET /api/v1/estado
     */
    public Mono<ServerResponse> listEstate(ServerRequest request) {
        log.info("Application recibida para listar Estados");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.listState()
                        .doOnNext(e -> log.debug("State listado: {}", e)), State.class);
    }
}
