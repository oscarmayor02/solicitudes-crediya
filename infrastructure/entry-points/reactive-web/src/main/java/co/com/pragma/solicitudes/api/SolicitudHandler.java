    package co.com.pragma.solicitudes.api;

    import co.com.pragma.solicitudes.model.solicitud.Solicitud;
    import co.com.pragma.solicitudes.usecase.solicitud.SolicitudUseCase;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.MediaType;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.server.ServerRequest;
    import org.springframework.web.reactive.function.server.ServerResponse;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    @Component
    @RequiredArgsConstructor
    public class SolicitudHandler {

        private final SolicitudUseCase useCase;

        /**
         * Crear nueva solicitud
         * POST /api/v1/solicitudes
         */
        public Mono<ServerResponse> crearSolicitud(ServerRequest request) {
            return request.bodyToMono(Solicitud.class)
                    .flatMap(useCase::ejecutar)
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(s));
        }

        /**
         * Listar todas las solicitudes
         * GET /api/v1/solicitudes
         */
        public Mono<ServerResponse> listarSolicitudes(ServerRequest request) {
            Flux<Solicitud> solicitudes = useCase.getAllSolicitudes();
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(solicitudes, Solicitud.class);
        }

        /**
         * Obtener solicitud por ID
         * GET /api/v1/solicitudes/{id}
         */
        public Mono<ServerResponse> obtenerPorId(ServerRequest request) {
            Long id = Long.valueOf(request.pathVariable("id"));
            return useCase.getSolicitudById(id)
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(s))
                    .switchIfEmpty(ServerResponse.notFound().build());
        }

        /**
         * Editar solicitud existente
         * PUT /api/v1/solicitudes
         */
        public Mono<ServerResponse> editarSolicitud(ServerRequest request) {
            return request.bodyToMono(Solicitud.class)
                    .flatMap(solicitud -> useCase.editSolicitud(solicitud))  // <- aquí
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(s));
        }

        /**
         * Eliminar solicitud por ID
         * DELETE /api/v1/solicitudes/{id}
         */
        public Mono<ServerResponse> eliminarSolicitud(ServerRequest request) {
            Long id = Long.valueOf(request.pathVariable("id"));
            return useCase.delete(id)
                    .then(ServerResponse.noContent().build());
        }


    }
