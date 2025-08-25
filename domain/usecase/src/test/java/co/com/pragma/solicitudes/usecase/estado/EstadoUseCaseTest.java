package co.com.pragma.solicitudes.usecase.estado;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.model.estado.gateways.EstadoRepository;
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
 * Test unitario para EstadoUseCase.
 * Aquí probamos el comportamiento de los casos de uso de forma aislada
 * usando Mockito para mockear dependencias y StepVerifier para verificar flujos reactivos.
 */
class EstadoUseCaseTest {

    private EstadoRepository estadoRepository; // Mock del repositorio reactivo
    private EstadoUseCase estadoUseCase; // Clase bajo prueba

    @BeforeEach
    void setup() {
        // Crear un mock de EstadoRepository usando Mockito
        estadoRepository = Mockito.mock(EstadoRepository.class);
        // Inyectar el mock en la clase de use case
        estadoUseCase = new EstadoUseCase(estadoRepository);
    }

    @Test
    void crearEstado_Success() {
        // Preparar datos de prueba
        Estado estado = new Estado(null, "Pendiente", "Estado inicial");
        Estado estadoGuardado = new Estado(1L, "Pendiente", "Estado inicial");

        // Mockear el método save para que devuelva un Mono con el estado guardado
        when(estadoRepository.save(any())).thenReturn(Mono.just(estadoGuardado));

        // Verificar comportamiento usando StepVerifier
        StepVerifier.create(estadoUseCase.createEstado(estado))
                // Esperamos que el Mono emita un estado cuyo id sea 1
                .expectNextMatches(e -> e.getIdEstado().equals(1L))
                // Luego el flujo se completa sin errores
                .verifyComplete();

        // Verificar que el método save se llamó exactamente una vez
        verify(estadoRepository, times(1)).save(estado);
    }

    @Test
    void crearEstado_FaltanDatos() {
        Estado estado = new Estado(null, null, null);

        // Si faltan datos, el Mono debe emitir un error
        StepVerifier.create(estadoUseCase.createEstado(estado))
                .expectErrorSatisfies(e ->
                        // Comprobamos que sea la excepción correcta
                        assertTrue(e instanceof DomainExceptions.DatosObligatorios)
                )
                .verify();
    }

    @Test
    void listarEstados_Success() {
        Estado e1 = new Estado(1L, "Pendiente", "Estado inicial");
        Estado e2 = new Estado(2L, "Aprobado", "Estado aprobado");

        // Mockear findAll para que devuelva un Flux con los estados
        when(estadoRepository.findAll()).thenReturn(Flux.fromIterable(List.of(e1, e2)));

        // Verificar el flujo de salida
        StepVerifier.create(estadoUseCase.listEstados())
                // Esperamos que emita los dos estados en orden
                .expectNext(e1, e2)
                .verifyComplete();

        // Verificar que findAll se llamó una vez
        verify(estadoRepository, times(1)).findAll();
    }
}
