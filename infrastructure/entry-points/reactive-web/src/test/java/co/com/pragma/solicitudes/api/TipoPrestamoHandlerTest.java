package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.usecase.tipoprestamo.TipoPrestamoUseCase;
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

class TipoPrestamoHandlerTest {

    private TipoPrestamoUseCase useCase;
    private TipoPrestamoHandler handler;
    private WebTestClient client;

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(TipoPrestamoUseCase.class);
        handler = new TipoPrestamoHandler(useCase);
        client = WebTestClient.bindToRouterFunction(new RouterRestTipoPrestamo().tipoPrestamoRoutes(handler)).build();
    }

    @Test
    void crearTipoPrestamo_Exitoso() {
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setNombre("Personal");
        tipo.setMontoMinimo(BigDecimal.valueOf(1000));
        tipo.setMontoMaximo(BigDecimal.valueOf(5000));
        tipo.setTasaInteres(BigDecimal.valueOf(2));

        when(useCase.crear(any())).thenReturn(Mono.just(tipo));

        client.post()
                .uri("/api/v1/tipos-prestamo")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tipo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TipoPrestamo.class)
                .isEqualTo(tipo);
    }

    @Test
    void listarTiposPrestamo_Exitoso() {
        TipoPrestamo t1 = new TipoPrestamo();
        TipoPrestamo t2 = new TipoPrestamo();
        when(useCase.listar()).thenReturn(Flux.just(t1, t2));

        client.get()
                .uri("/api/v1/tipos-prestamo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TipoPrestamo.class)
                .hasSize(2);
    }
}
