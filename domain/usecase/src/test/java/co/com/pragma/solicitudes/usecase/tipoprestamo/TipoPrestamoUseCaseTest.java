package co.com.pragma.solicitudes.usecase.tipoprestamo;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para TipoPrestamoUseCase.
 * Validamos creación de tipos de préstamo, errores y listados.
 */
class TipoPrestamoUseCaseTest {

    private TipoPrestamoRepository repository;
    private TipoPrestamoUseCase useCase;

    @BeforeEach
    void setup() {
        repository = mock(TipoPrestamoRepository.class);
        useCase = new TipoPrestamoUseCase(repository);
    }

    @Test
    void crearTipoPrestamo_Success() {
        TipoPrestamo tipo = TipoPrestamo.builder()
                .nombre("Personal")
                .montoMinimo(new BigDecimal("1000"))
                .montoMaximo(new BigDecimal("5000"))
                .tasaInteres(new BigDecimal("5"))
                .validacionAutomatica(true)
                .build();

        when(repository.existsByNombreIgnoreCase("Personal")).thenReturn(Mono.just(false));
        when(repository.save(any(TipoPrestamo.class))).thenReturn(Mono.just(tipo));

        StepVerifier.create(useCase.crear(tipo))
                .expectNext(tipo)
                .verifyComplete();

        verify(repository, times(1)).existsByNombreIgnoreCase("Personal");
        verify(repository, times(1)).save(tipo);
    }

    @Test
    void crearTipoPrestamo_ErrorNombreExistente() {
        TipoPrestamo tipo = TipoPrestamo.builder()
                .nombre("Personal")
                .montoMinimo(new BigDecimal("1000"))
                .montoMaximo(new BigDecimal("5000"))
                .tasaInteres(new BigDecimal("5"))
                .build();

        when(repository.existsByNombreIgnoreCase("Personal")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.crear(tipo))
                .expectErrorMatches(throwable ->
                        throwable instanceof DomainExceptions.DatosObligatorios &&
                                throwable.getMessage().equals("Ya existe un tipo de préstamo con ese nombre"))
                .verify();

        verify(repository, times(1)).existsByNombreIgnoreCase("Personal");
        verify(repository, never()).save(any());
    }

    @Test
    void listarTiposPrestamo_Success() {
        TipoPrestamo tipo1 = TipoPrestamo.builder().nombre("Personal").build();
        TipoPrestamo tipo2 = TipoPrestamo.builder().nombre("Hipotecario").build();

        when(repository.findAll()).thenReturn(Flux.just(tipo1, tipo2));

        StepVerifier.create(useCase.listar())
                .expectNext(tipo1, tipo2)
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_Success() {
        TipoPrestamo tipo = TipoPrestamo.builder().nombre("Personal").build();
        when(repository.findById(1L)).thenReturn(Mono.just(tipo));

        StepVerifier.create(useCase.obtenerPorId(1L))
                .expectNext(tipo)
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_NoExiste() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.obtenerPorId(99L))
                .expectErrorMatches(throwable ->
                        throwable instanceof DomainExceptions.TipoPrestamoNoExiste &&
                                throwable.getMessage().equals("Tipo de préstamo no encontrado"))
                .verify();

        verify(repository, times(1)).findById(99L);
    }
}
