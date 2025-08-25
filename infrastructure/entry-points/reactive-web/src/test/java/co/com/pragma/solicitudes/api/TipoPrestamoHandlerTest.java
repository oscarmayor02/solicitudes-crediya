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

    private TipoPrestamoUseCase useCase; // Mock del caso de uso
    private TipoPrestamoHandler handler; // Handler a testear
    private WebTestClient client; // Cliente WebTestClient

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(TipoPrestamoUseCase.class); // Creamos mock
        handler = new TipoPrestamoHandler(useCase); // Inyectamos mock en handler
        client = WebTestClient.bindToRouterFunction(
                new RouterRestTipoPrestamo().tipoPrestamoRoutes(handler)).build();
        // Cliente ligado al router de TipoPrestamo
    }

    @Test
    void crearTipoPrestamo_Exitoso() {
        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setNombre("Personal");
        tipo.setMontoMinimo(BigDecimal.valueOf(1000));
        tipo.setMontoMaximo(BigDecimal.valueOf(5000));
        tipo.setTasaInteres(BigDecimal.valueOf(2)); // Valores de prueba

        when(useCase.crear(any())).thenReturn(Mono.just(tipo));
        // Mock: al crear, devuelve el tipo prestamo

        client.post() // POST
                .uri("/api/v1/tipos-prestamo") // Endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tipo) // Body JSON
                .exchange()
                .expectStatus().isOk()
                .expectBody(TipoPrestamo.class)
                .isEqualTo(tipo); // Compara respuesta con mock
    }

    @Test
    void listarTiposPrestamo_Exitoso() {
        TipoPrestamo t1 = new TipoPrestamo();
        TipoPrestamo t2 = new TipoPrestamo();
        when(useCase.listar()).thenReturn(Flux.just(t1, t2));
        // Mock: lista de tipos de prestamo

        client.get()
                .uri("/api/v1/tipos-prestamo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TipoPrestamo.class)
                .hasSize(2); // Verifica que la lista tenga 2 elementos
    }
}
