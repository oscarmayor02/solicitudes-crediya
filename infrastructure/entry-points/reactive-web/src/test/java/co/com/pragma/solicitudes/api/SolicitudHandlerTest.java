package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.solicitud.Solicitud; // Modelo de dominio
import co.com.pragma.solicitudes.usecase.solicitud.SolicitudUseCase; // Caso de uso mockeado
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient; // Cliente de pruebas WebFlux
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal; // Para definir montos

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudHandlerTest {

    private SolicitudUseCase useCase; // Mock del caso de uso
    private SolicitudHandler handler; // Handler que vamos a testear
    private WebTestClient client; // Cliente WebTestClient

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(SolicitudUseCase.class); // Creamos mock
        handler = new SolicitudHandler(useCase); // Inyectamos mock al handler
        client = WebTestClient.bindToRouterFunction(new RouterRest().solicitudRoutes(handler)).build();
        // Cliente de pruebas ligado al router de Solicitudes
    }

    @Test
    void crearSolicitud_Exitoso() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000)); // Monto de prueba

        when(useCase.ejecutar(any())).thenReturn(Mono.just(solicitud));
        // Mock: al ejecutar el caso de uso, devuelve la solicitud

        client.post() // POST
                .uri("/api/v1/solicitudes") // Endpoint
                .contentType(MediaType.APPLICATION_JSON) // Tipo JSON
                .bodyValue(solicitud) // Body de la solicitud
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera 200 OK
                .expectBody(Solicitud.class) // Espera objeto Solicitud
                .isEqualTo(solicitud); // Compara con el objeto esperado
    }

    @Test
    void listarSolicitudes_Exitoso() {
        Solicitud s1 = new Solicitud();
        Solicitud s2 = new Solicitud();
        when(useCase.getAllSolicitudes()).thenReturn(Flux.just(s1, s2));
        // Mock: devuelve dos solicitudes en un Flux

        client.get() // GET
                .uri("/api/v1/solicitudes") // Endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Solicitud.class) // Lista de solicitudes
                .hasSize(2); // Verifica que sean 2
    }
}
