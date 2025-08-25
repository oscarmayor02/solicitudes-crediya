package co.com.pragma.solicitudes.api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRestTipoPrestamo {

    @Bean
    public RouterFunction<ServerResponse> tipoPrestamoRoutes(TipoPrestamoHandler handler) {
        return route(POST(TipoPrestamoHandler.RUTA_TIPO_PRESTAMO), handler::crear)
                .andRoute(GET(TipoPrestamoHandler.RUTA_TIPO_PRESTAMO), handler::listar)
                .andRoute(GET(TipoPrestamoHandler.RUTA_TIPO_PRESTAMO + "/{id}"), handler::obtenerPorId);
    }
}
