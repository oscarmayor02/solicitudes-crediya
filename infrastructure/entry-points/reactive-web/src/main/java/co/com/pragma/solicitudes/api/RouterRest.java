package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Solicitudes API", description = "Operaciones CRUD para solicitudes de crédito")
public class RouterRest {

    @Bean
    @RouterOperations({
            // CREATE
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "createApplication",
                    operation = @Operation(
                            operationId = "crearSolicitud",
                            summary = "Crear nueva solicitud",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = Application.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Application creada exitosamente")
                            }
                    )
            ),
            // LIST
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "listApplications",
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Obtener todas las solicitudes",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de solicitudes",
                                            content = @Content(schema = @Schema(implementation = Application.class)))
                            }
                    )
            ),
            // GET BY ID
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD + "/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "getById",
                    operation = @Operation(
                            operationId = "obtenerSolicitudPorId",
                            summary = "Obtener una solicitud por su ID",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Application encontrada"),
                                    @ApiResponse(responseCode = "404", description = "Application no encontrada")
                            }
                    )
            ),
            // UPDATE
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "editApplication",
                    operation = @Operation(
                            operationId = "editarSolicitud",
                            summary = "Editar solicitud existente",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Application actualizada"),
                                    @ApiResponse(responseCode = "404", description = "Application no encontrada")
                            }
                    )
            ),
            // DELETE
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD + "/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "deleteApplication",
                    operation = @Operation(
                            operationId = "eliminarSolicitud",
                            summary = "Eliminar solicitud por ID",
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Application eliminada"),
                                    @ApiResponse(responseCode = "404", description = "Application no encontrada")
                            }
                    )
            ),
            // REVIEW
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD_REVISION,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "listReviewApplication",
                    operation = @Operation(
                            operationId = "listarSolicitudesRevision",
                            summary = "Listar solicitudes en revisión manual",
                            description = "Si no se envía `estado` muestra por defecto PENDIENTE_REVISION",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de solicitudes en revisión")
                            }
                    )
            ),
            @RouterOperation(
                    path = ApplicationConstants.RUTA_SOLICITUD_DECISION,   // "/api/v1/solicitud"
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = ApplicationHandler.class,
                    beanMethod = "decide",
                    operation = @Operation(
                            operationId = "decideApplication",
                            summary = "Aprobar o rechazar solicitud (rol ASESOR)",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = co.com.pragma.solicitudes.api.dto.DecisionRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Decisión aplicada y evento encolado"),
                                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                                    @ApiResponse(responseCode = "403", description = "Sin permisos"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )})
    public RouterFunction<ServerResponse> applicationRoutes(ApplicationHandler handler) {
        return route(POST(ApplicationConstants.RUTA_SOLICITUD), handler::createApplication)
                .andRoute(GET(ApplicationConstants.RUTA_SOLICITUD_REVISION), handler::listReviewApplication)
                .andRoute(GET(ApplicationConstants.RUTA_SOLICITUD), handler::listApplications)
                .andRoute(GET(ApplicationConstants.RUTA_SOLICITUD + "/{id}"), handler::getById)
                .andRoute(PUT(ApplicationConstants.RUTA_SOLICITUD), handler::editApplication)
                .andRoute(PUT(ApplicationConstants.RUTA_SOLICITUD_DECISION), handler::decide)
                .andRoute(DELETE(ApplicationConstants.RUTA_SOLICITUD + "/{id}"), handler::deleteApplication);
    }
}
