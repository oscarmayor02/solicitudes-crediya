package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.usecase.estado.EstadoUseCase;
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
public class EstadoHandler {

    private final EstadoUseCase useCase; // Caso de uso para manejar la lógica de negocio
    private static final Logger log = LoggerFactory.getLogger(EstadoHandler.class); // Logger para trazabilidad

    // Constantes para rutas
    public static final String RUTA_ESTADO = "/api/v1/estado";

    /**
     * Crear nuevo estado
     * POST /api/v1/estado
     */
    public Mono<ServerResponse> createEstado(ServerRequest request) {
        log.info("Solicitud recibida para crear Estado"); // Log de trazabilidad

        return request.bodyToMono(Estado.class) // Convertimos el body JSON a objeto Estado
                .doOnNext(e -> log.debug("Cuerpo recibido: {}", e))
                .flatMap(useCase::createEstado) // Llamada al caso de uso
                .flatMap(saved -> {
                    log.info("Estado creado con éxito: {}", saved);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(saved);
                });
    }

    /**
     * Listar todos los estados
     * GET /api/v1/estado
     */
    public Mono<ServerResponse> listEstados(ServerRequest request) {
        log.info("Solicitud recibida para listar Estados");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.listEstados()
                        .doOnNext(e -> log.debug("Estado listado: {}", e)), Estado.class);
    }
}
