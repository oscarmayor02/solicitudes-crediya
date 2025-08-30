package co.com.pragma.solicitudes.api;


import co.com.pragma.solicitudes.model.loantype.LoanType;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
/**
 *  Router funcional para exponer endpoints relacionados con Tipos de Préstamo.
 */
@Configuration
@Tag(name = "Tipos de Préstamo API", description = "Operaciones CRUD para tipos de préstamo")
public class RouterRestLoanType {


    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = LoanTypeHandler.RUTA_TIPO_PRESTAMO,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = LoanTypeHandler.class,
                    beanMethod = "crear",
                    operation = @Operation(
                            operationId = "crearTipoPrestamo",
                            summary = "Crear nuevo tipo de préstamo",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = LoanType.class))
                            ),
                            responses = {@ApiResponse(responseCode = "201", description = "Tipo de préstamo creado")}
                    )
            ),
            @RouterOperation(
                    path = LoanTypeHandler.RUTA_TIPO_PRESTAMO,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = LoanTypeHandler.class,
                    beanMethod = "listar",
                    operation = @Operation(
                            operationId = "listarTiposPrestamo",
                            summary = "Listar todos los tipos de préstamo",
                            responses = {@ApiResponse(responseCode = "200", description = "Lista de tipos de préstamo",
                                    content = @Content(schema = @Schema(implementation = LoanType.class)))}
                    )
            ),
            @RouterOperation(
                    path = LoanTypeHandler.RUTA_TIPO_PRESTAMO + "/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = LoanTypeHandler.class,
                    beanMethod = "obtenerPorId",
                    operation = @Operation(
                            operationId = "obtenerTipoPrestamoPorId",
                            summary = "Obtener un tipo de préstamo por ID",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Tipo de préstamo encontrado"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> loanTypeRoutes(LoanTypeHandler handler) {
        return route(POST(LoanTypeHandler.RUTA_TIPO_PRESTAMO), handler::createLoanType)
                .andRoute(GET(LoanTypeHandler.RUTA_TIPO_PRESTAMO), handler::list)
                .andRoute(GET(LoanTypeHandler.RUTA_TIPO_PRESTAMO + "/{id}"), handler::getById);
    }
}
