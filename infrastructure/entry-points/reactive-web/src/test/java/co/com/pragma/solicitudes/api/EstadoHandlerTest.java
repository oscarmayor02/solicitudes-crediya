package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.usecase.estado.EstadoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstadoHandlerTest {

    private EstadoUseCase useCase;
    private EstadoHandler handler;
    private WebTestClient client;

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(EstadoUseCase.class);
        handler = new EstadoHandler(useCase);
        client = WebTestClient.bindToRouterFunction(new RouterRestEstado().estadoRoutes(handler)).build();
    }


    @Test
    void createEstado_Exitoso() {
        Estado estado = new Estado();
        estado.setNombre("Pendiente");

        when(useCase.createEstado(any())).thenReturn(Mono.just(estado));

        client.post()
                .uri("/api/v1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(estado)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Estado.class)
                .isEqualTo(estado);
    }

    @Test
    void listEstados_Exitoso() {
        Estado estado1 = new Estado();
        Estado estado2 = new Estado();
        when(useCase.listEstados()).thenReturn(Flux.just(estado1, estado2));

        client.get()
                .uri("/api/v1/estado")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Estado.class)
                .hasSize(2);
    }
}
