package co.com.pragma.solicitudes.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class RouterRestEstado {

    @Bean
    public RouterFunction<ServerResponse> estadoRoutes(EstadoHandler handler) {
        // Definimos rutas usando constantes del handler
        return route(POST(EstadoHandler.RUTA_ESTADO), handler::createEstado)
                .andRoute(GET(EstadoHandler.RUTA_ESTADO), handler::listEstados);
    }
}

