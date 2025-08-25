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

class EstadoUseCaseTest {

    private EstadoRepository estadoRepository;
    private EstadoUseCase estadoUseCase;

    @BeforeEach
    void setup() {
        estadoRepository = Mockito.mock(EstadoRepository.class);
        estadoUseCase = new EstadoUseCase(estadoRepository);
    }

    @Test
    void crearEstado_Success() {
        Estado estado = new Estado(null, "Pendiente", "Estado inicial");
        Estado estadoGuardado = new Estado(1L, "Pendiente", "Estado inicial");

        when(estadoRepository.save(any())).thenReturn(Mono.just(estadoGuardado));

        StepVerifier.create(estadoUseCase.createEstado(estado))
                .expectNextMatches(e -> e.getIdEstado().equals(1L))
                .verifyComplete();

        verify(estadoRepository, times(1)).save(estado);
    }

    @Test
    void crearEstado_FaltanDatos() {
        Estado estado = new Estado(null, null, null);

        StepVerifier.create(estadoUseCase.createEstado(estado))
                .expectErrorSatisfies(e -> assertTrue(e instanceof DomainExceptions.DatosObligatorios))
                .verify();
    }

    @Test
    void listarEstados_Success() {
        Estado e1 = new Estado(1L, "Pendiente", "Estado inicial");
        Estado e2 = new Estado(2L, "Aprobado", "Estado aprobado");

        when(estadoRepository.findAll()).thenReturn(Flux.fromIterable(List.of(e1, e2)));

        StepVerifier.create(estadoUseCase.listEstados())
                .expectNext(e1, e2)
                .verifyComplete();

        verify(estadoRepository, times(1)).findAll();
    }
}
