package co.com.pragma.solicitudes.api; // Paquete de tests de la capa API

// Importaciones necesarias para testing
import co.com.pragma.solicitudes.model.estado.Estado; // Modelo de dominio
import co.com.pragma.solicitudes.usecase.estado.EstadoUseCase; // Caso de uso simulado
import org.junit.jupiter.api.BeforeEach; // Anotación para ejecutar antes de cada test
import org.junit.jupiter.api.Test; // Anotación para cada test
import org.mockito.Mockito; // Mockito para mocks
import org.springframework.http.MediaType; // Tipos de contenido HTTP
import org.springframework.test.web.reactive.server.WebTestClient; // Cliente de pruebas WebFlux
import reactor.core.publisher.Flux; // Flujo 0..N elementos
import reactor.core.publisher.Mono; // Flujo 0..1 elementos

import static org.mockito.ArgumentMatchers.any; // Matcher para cualquier argumento
import static org.mockito.Mockito.*; // Import estático de Mockito

class EstadoHandlerTest {

    private EstadoUseCase useCase; // Mock del caso de uso
    private EstadoHandler handler; // Handler que vamos a testear
    private WebTestClient client; // Cliente de pruebas HTTP para WebFlux

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(EstadoUseCase.class); // Creamos mock del caso de uso
        handler = new EstadoHandler(useCase); // Creamos el handler con el mock inyectado
        // Creamos un cliente WebTestClient que apunta al router del handler
        client = WebTestClient.bindToRouterFunction(new RouterRestEstado().estadoRoutes(handler)).build();
    }

    @Test
    void createEstado_Exitoso() {
        Estado estado = new Estado(); // Creamos un objeto Estado de prueba
        estado.setNombre("Pendiente"); // Definimos el nombre

        when(useCase.createEstado(any())).thenReturn(Mono.just(estado));
        // Mockeamos el caso de uso para que devuelva el estado dentro de un Mono

        client.post() // Realiza un POST
                .uri("/api/v1/estado") // URI del endpoint
                .contentType(MediaType.APPLICATION_JSON) // Tipo de contenido JSON
                .bodyValue(estado) // Body de la solicitud
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera código 200 OK
                .expectBody(Estado.class) // Espera que el body sea del tipo Estado
                .isEqualTo(estado); // Verifica que la respuesta sea igual al estado esperado
    }

    @Test
    void listEstados_Exitoso() {
        Estado estado1 = new Estado(); // Primer estado de prueba
        Estado estado2 = new Estado(); // Segundo estado de prueba
        when(useCase.listEstados()).thenReturn(Flux.just(estado1, estado2));
        // Mockeamos que el caso de uso devuelva los dos estados en un flujo

        client.get() // Realiza GET
                .uri("/api/v1/estado") // URI del endpoint
                .exchange() // Ejecuta la petición
                .expectStatus().isOk() // Espera 200 OK
                .expectBodyList(Estado.class) // Espera un listado de Estados
                .hasSize(2); // Verifica que haya 2 elementos en la respuesta
    }
}
