package co.com.pragma.solicitudes.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration // Marca la clase como configuración de rutas
public class RouterRest {

    /**
     * Define las rutas HTTP para solicitudes
     */
    @Bean
    public RouterFunction<ServerResponse> solicitudRoutes(SolicitudHandler handler) {
        return route(POST(SolicitudHandler.RUTA_SOLICITUD), handler::crearSolicitud) // POST -> crear
                .andRoute(GET(SolicitudHandler.RUTA_SOLICITUD), handler::listarSolicitudes) // GET -> listar
                .andRoute(GET(SolicitudHandler.RUTA_SOLICITUD + "/{id}"), handler::obtenerPorId) // GET por ID
                .andRoute(PUT(SolicitudHandler.RUTA_SOLICITUD), handler::editarSolicitud) // PUT -> editar
                .andRoute(DELETE(SolicitudHandler.RUTA_SOLICITUD + "/{id}"), handler::eliminarSolicitud); // DELETE por ID
    }
}
