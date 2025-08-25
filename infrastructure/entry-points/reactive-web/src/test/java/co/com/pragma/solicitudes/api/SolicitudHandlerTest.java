package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudHandlerTest {

    private SolicitudUseCase useCase;
    private SolicitudHandler handler;
    private WebTestClient client;

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(SolicitudUseCase.class);
        handler = new SolicitudHandler(useCase);
        client = WebTestClient.bindToRouterFunction(new RouterRest().solicitudRoutes(handler)).build();
    }

    @Test
    void crearSolicitud_Exitoso() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000));

        when(useCase.ejecutar(any())).thenReturn(Mono.just(solicitud));

        client.post()
                .uri("/api/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitud)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Solicitud.class)
                .isEqualTo(solicitud);
    }

    @Test
    void listarSolicitudes_Exitoso() {
        Solicitud s1 = new Solicitud();
        Solicitud s2 = new Solicitud();
        when(useCase.getAllSolicitudes()).thenReturn(Flux.just(s1, s2));

        client.get()
                .uri("/api/v1/solicitudes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Solicitud.class)
                .hasSize(2);
    }
}
