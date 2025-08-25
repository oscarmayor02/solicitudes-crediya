package co.com.pragma.solicitudes.api; // Paquete de la capa API

// Importaciones necesarias
import co.com.pragma.solicitudes.model.solicitud.Solicitud; // Modelo de dominio
import co.com.pragma.solicitudes.usecase.solicitud.SolicitudUseCase; // Caso de uso con la lógica de negocio
import lombok.RequiredArgsConstructor; // Genera constructor con atributos final automáticamente
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Para trazabilidad/logs
import org.springframework.http.MediaType; // Para indicar tipo de contenido JSON
import org.springframework.stereotype.Component; // Marca clase como bean de Spring
import org.springframework.web.reactive.function.server.ServerRequest; // Request de WebFlux
import org.springframework.web.reactive.function.server.ServerResponse; // Response de WebFlux
import reactor.core.publisher.Flux; // Flujo reactivo 0..N elementos
import reactor.core.publisher.Mono; // Flujo reactivo 0..1 elementos

@Component // Bean de Spring
@RequiredArgsConstructor // Genera constructor con atributos final
public class SolicitudHandler {

    private final SolicitudUseCase useCase; // Caso de uso inyectado
    private static final Logger log = LoggerFactory.getLogger(SolicitudHandler.class); // Logger para trazabilidad
    public static final String RUTA_SOLICITUD = "/api/v1/solicitudes"; // Constante de ruta

    /**
     * Crear nueva solicitud
     * POST /api/v1/solicitudes
     */
    public Mono<ServerResponse> crearSolicitud(ServerRequest request) {
        log.info("Solicitud recibida para crear nueva Solicitud"); // Log de entrada
        return request.bodyToMono(Solicitud.class) // Convierte el body JSON a objeto Solicitud
                .doOnNext(s -> log.debug("Cuerpo recibido: {}", s)) // Log del contenido recibido
                .flatMap(useCase::ejecutar) // Llama al caso de uso para crear la solicitud
                .flatMap(s -> { // Cuando se completa la creación
                    log.info("Solicitud creada con éxito: {}", s); // Log de éxito
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON) // Devuelve JSON
                            .bodyValue(s); // Body con la solicitud creada
                });
    }

    /**
     * Listar todas las solicitudes
     * GET /api/v1/solicitudes
     */
    public Mono<ServerResponse> listarSolicitudes(ServerRequest request) {
        log.info("Solicitud para listar todas las solicitudes"); // Log de entrada
        Flux<Solicitud> solicitudes = useCase.getAllSolicitudes(); // Obtiene todas las solicitudes
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON) // Devuelve JSON
                .body(solicitudes, Solicitud.class); // Body con todas las solicitudes
    }

    /**
     * Obtener solicitud por ID
     * GET /api/v1/solicitudes/{id}
     */
    public Mono<ServerResponse> obtenerPorId(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id")); // Extrae ID de la ruta
        log.info("Solicitud para obtener solicitud con ID: {}", id); // Log con ID
        return useCase.getSolicitudById(id) // Llama al caso de uso para buscar por ID
                .flatMap(s -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s)) // Devuelve la solicitud si existe
                .switchIfEmpty(ServerResponse.notFound().build()); // Si no existe, 404
    }

    /**
     * Editar solicitud existente
     * PUT /api/v1/solicitudes
     */
    public Mono<ServerResponse> editarSolicitud(ServerRequest request) {
        log.info("Solicitud para editar solicitud"); // Log de entrada
        return request.bodyToMono(Solicitud.class) // Convierte JSON a objeto
                .flatMap(useCase::editSolicitud) // Llama al caso de uso para editar
                .flatMap(s -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s)); // Devuelve solicitud editada
    }

    /**
     * Eliminar solicitud por ID
     * DELETE /api/v1/solicitudes/{id}
     */
    public Mono<ServerResponse> eliminarSolicitud(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id")); // Extrae ID de la ruta
        log.info("Solicitud para eliminar solicitud con ID: {}", id); // Log de entrada
        return useCase.delete(id) // Llama al caso de uso para eliminar
                .then(ServerResponse.noContent().build()); // Devuelve 204 No Content
    }
}
