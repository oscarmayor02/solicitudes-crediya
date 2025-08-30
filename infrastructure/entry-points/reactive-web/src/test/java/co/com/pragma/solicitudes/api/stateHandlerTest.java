package co.com.pragma.solicitudes.api; // Paquete de tests de la capa API

// Importaciones necesarias para testing
import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.usecase.state.StateUseCase; // Caso de uso simulado
import org.junit.jupiter.api.BeforeEach; // Anotación para ejecutar antes de cada test
import org.junit.jupiter.api.Test; // Anotación para cada test
import org.mockito.Mockito; // Mockito para mocks
import org.springframework.http.MediaType; // Tipos de contenido HTTP
import org.springframework.test.web.reactive.server.WebTestClient; // Cliente de pruebas WebFlux
import reactor.core.publisher.Flux; // Flujo 0..N elementos
import reactor.core.publisher.Mono; // Flujo 0..1 elementos

import static org.mockito.ArgumentMatchers.any; // Matcher para cualquier argumento
import static org.mockito.Mockito.*; // Import estático de Mockito

class stateHandlerTest {

    private StateUseCase useCase; // Mock del caso de uso
    private StateHandler handler; // Handler que vamos a testear
    private WebTestClient client; // Cliente de pruebas HTTP para WebFlux

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(StateUseCase.class); // Creamos mock del caso de uso
        handler = new StateHandler(useCase); // Creamos el handler con el mock inyectado
        // Creamos un cliente WebTestClient que apunta al router del handler
        client = WebTestClient.bindToRouterFunction(new RouterRestState().stateRoutes(handler)).build();
    }

    @Test
    void createEstado_Exitoso() {
        State state = new State(); // Creamos un objeto State de prueba
        state.setName("Pendiente"); // Definimos el nombre

        when(useCase.createState(any())).thenReturn(Mono.just(state));
        // Mockeamos el caso de uso para que devuelva el State dentro de un Mono

        client.post() // Realiza un POST
                .uri("/api/v1/estado") // URI del endpoint
                .contentType(MediaType.APPLICATION_JSON) // Tipo de contenido JSON
                .bodyValue(state) // Body de la solicitud
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera código 200 OK
                .expectBody(State.class) // Espera que el body sea del tipo State
                .isEqualTo(state); // Verifica que la respuesta sea igual al State esperado
    }

    @Test
    void listEstados_Exitoso() {
        State state1 = new State(); // Primer estado de prueba
        State state2 = new State(); // Segundo estado de prueba
        when(useCase.listState()).thenReturn(Flux.just(state1, state2));
        // Mockeamos que el caso de uso devuelva los dos estados en un flujo

        client.get() // Realiza GET
                .uri("/api/v1/estado") // URI del endpoint
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera 200 OK
                .expectBodyList(State.class) // Espera un listado de Estados
                .hasSize(2); // Verifica que haya 2 elementos en la respuesta
    }
}
