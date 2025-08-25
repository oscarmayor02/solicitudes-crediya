package co.com.pragma.solicitudes.api;
import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.usecase.estado.EstadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EstadoHandler {

    private final EstadoUseCase useCase;

    public Mono<ServerResponse> createEstado(ServerRequest request) {
        return request.bodyToMono(Estado.class)
                .flatMap(useCase::createEstado)
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    public Mono<ServerResponse> listEstados(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.listEstados(), Estado.class);
    }
}