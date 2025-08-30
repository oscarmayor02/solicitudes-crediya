package co.com.pragma.solicitudes.usecase.state;

import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.model.state.gateways.StateRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para StateUseCase.
 * Aquí probamos el comportamiento de los casos de uso de forma aislada
 * usando Mockito para mockear dependencias y StepVerifier para verificar flujos reactivos.
 */
class stateUseCaseTest {

    private StateRepository stateRepository; // Mock del repositorio reactivo
    private StateUseCase stateUseCase; // Clase bajo prueba

    @BeforeEach
    void setup() {
        // Crear un mock de StateRepository usando Mockito
        stateRepository = Mockito.mock(StateRepository.class);
        // Inyectar el mock en la clase de use case
        stateUseCase = new StateUseCase(stateRepository);
    }

    @Test
    void crearEstado_Success() {
        // Preparar datos de prueba
        State state = new State(null, "Pendiente", "State inicial");
        State stateGuardado = new State(1L, "Pendiente", "State inicial");

        // Mockear el método save para que devuelva un Mono con el State guardado
        when(stateRepository.save(any())).thenReturn(Mono.just(stateGuardado));

        // Verificar comportamiento usando StepVerifier
        StepVerifier.create(stateUseCase.createState(state))
                // Esperamos que el Mono emita un State cuyo id sea 1
                .expectNextMatches(e -> e.getIdState().equals(1L))
                // Luego el flujo se completa sin errores
                .verifyComplete();

        // Verificar que el método save se llamó exactamente una vez
        verify(stateRepository, times(1)).save(state);
    }

    @Test
    void crearEstado_FaltanDatos() {
        State state = new State(null, null, null);

        // Si faltan datos, el Mono debe emitir un error
        StepVerifier.create(stateUseCase.createState(state))
                .expectErrorSatisfies(e ->
                        // Comprobamos que sea la excepción correcta
                        assertTrue(e instanceof DomainExceptions.DatosObligatorios)
                )
                .verify();
    }

    @Test
    void listarEstados_Success() {
        State e1 = new State(1L, "Pendiente", "State inicial");
        State e2 = new State(2L, "Aprobado", "State aprobado");

        // Mockear findAll para que devuelva un Flux con los estados
        when(stateRepository.findAll()).thenReturn(Flux.fromIterable(List.of(e1, e2)));

        // Verificar el flujo de salida
        StepVerifier.create(stateUseCase.listState())
                // Esperamos que emita los dos estados en orden
                .expectNext(e1, e2)
                .verifyComplete();

        // Verificar que findAll se llamó una vez
        verify(stateRepository, times(1)).findAll();
    }
}
