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
        return route(POST("/api/v1/estado"), handler::createEstado)
                .andRoute(GET("/api/v1/estado"), handler::listEstados);
    }

}
