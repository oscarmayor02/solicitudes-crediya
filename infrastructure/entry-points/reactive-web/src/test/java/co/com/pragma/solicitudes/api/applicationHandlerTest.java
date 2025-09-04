package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.usecase.application.ApplicationUseCase; // Caso de uso mockeado
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient; // Cliente de pruebas WebFlux
import reactor.core.publisher.Flux;

import java.math.BigDecimal; // Para definir montos

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class applicationHandlerTest {

    private ApplicationUseCase useCase; // Mock del caso de uso
    private ApplicationHandler handler; // Handler que vamos a testear
    private WebTestClient client; // Cliente WebTestClient

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(ApplicationUseCase.class); // Creamos mock
        //handler = new ApplicationHandler(useCase); // Inyectamos mock al handler
        client = WebTestClient.bindToRouterFunction(new RouterRest().applicationRoutes(handler)).build();
        // Cliente de pruebas ligado al router de Solicitudes
    }

    @Test
    void crearSolicitud_Exitoso() {
        Application application = new Application();
        application.setAmount(BigDecimal.valueOf(1000)); // Monto de prueba

      //  when(useCase.ejecutar(any())).thenReturn(Mono.just(Application));
        // Mock: al ejecutar el caso de uso, devuelve la Application

        client.post() // POST
                .uri("/api/v1/solicitudes") // Endpoint
                .contentType(MediaType.APPLICATION_JSON) // Tipo JSON
                .bodyValue(application) // Body de la Application
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera 200 OK
                .expectBody(Application.class) // Espera objeto Application
                .isEqualTo(application); // Compara con el objeto esperado
    }

    @Test
    void listarSolicitudes_Exitoso() {
        Application s1 = new Application();
        Application s2 = new Application();
        when(useCase.getAllApplication()).thenReturn(Flux.just(s1, s2));
        // Mock: devuelve dos solicitudes en un Flux

        client.get() // GET
                .uri("/api/v1/solicitudes") // Endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Application.class) // Lista de solicitudes
                .hasSize(2); // Verifica que sean 2
    }
}
