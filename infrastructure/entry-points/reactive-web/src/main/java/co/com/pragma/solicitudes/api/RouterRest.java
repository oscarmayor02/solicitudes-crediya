package co.com.pragma.solicitudes.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> solicitudRoutes(SolicitudHandler handler) {
        return route(POST("/api/v1/solicitudes"), handler::crearSolicitud)
                .andRoute(GET("/api/v1/solicitudes"), handler::listarSolicitudes)
                .andRoute(GET("/api/v1/solicitudes/{id}"), handler::obtenerPorId)
                .andRoute(PUT("/api/v1/solicitudes"), handler::editarSolicitud)
                .andRoute(DELETE("/api/v1/solicitudes/{id}"), handler::eliminarSolicitud);

    }
}
