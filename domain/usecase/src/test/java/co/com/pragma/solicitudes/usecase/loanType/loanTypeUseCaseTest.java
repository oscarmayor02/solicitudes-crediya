package co.com.pragma.solicitudes.usecase.loanType;

import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para LoanTypeUseCase.
 * Validamos creación de tipos de préstamo, errores y listados.
 */
class loanTypeUseCaseTest {

    private LoanTypeRepository repository;
    private LoanTypeUseCase useCase;

    @BeforeEach
    void setup() {
        repository = mock(LoanTypeRepository.class);
        useCase = new LoanTypeUseCase(repository);
    }

    @Test
    void crearTipoPrestamo_Success() {
        LoanType tipo = LoanType.builder()
                .name("Personal")
                .minimumAmount(new BigDecimal("1000"))
                .maximumAmount(new BigDecimal("5000"))
                .rateInterest(new BigDecimal("5"))
                .automaticValidation(true)
                .build();

        when(repository.existsByNameIgnoreCase("Personal")).thenReturn(Mono.just(false));
        when(repository.save(any(LoanType.class))).thenReturn(Mono.just(tipo));

        StepVerifier.create(useCase.create(tipo))
                .expectNext(tipo)
                .verifyComplete();

        verify(repository, times(1)).existsByNameIgnoreCase("Personal");
        verify(repository, times(1)).save(tipo);
    }

    @Test
    void crearTipoPrestamo_ErrorNombreExistente() {
        LoanType tipo = LoanType.builder()
                .name("Personal")
                .minimumAmount(new BigDecimal("1000"))
                .maximumAmount(new BigDecimal("5000"))
                .rateInterest(new BigDecimal("5"))
                .build();

        when(repository.existsByNameIgnoreCase("Personal")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.create(tipo))
                .expectErrorMatches(throwable ->
                        throwable instanceof DomainExceptions.DatosObligatorios &&
                                throwable.getMessage().equals("Ya existe un tipo de préstamo con ese nombre"))
                .verify();

        verify(repository, times(1)).existsByNameIgnoreCase("Personal");
        verify(repository, never()).save(any());
    }

    @Test
    void listarTiposPrestamo_Success() {
        LoanType tipo1 = LoanType.builder().name("Personal").build();
        LoanType tipo2 = LoanType.builder().name("Hipotecario").build();

        when(repository.findAll()).thenReturn(Flux.just(tipo1, tipo2));

        StepVerifier.create(useCase.listLoanType())
                .expectNext(tipo1, tipo2)
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_Success() {
        LoanType tipo = LoanType.builder().name("Personal").build();
        when(repository.findById(1L)).thenReturn(Mono.just(tipo));

        StepVerifier.create(useCase.getById(1L))
                .expectNext(tipo)
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_NoExiste() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getById(99L))
                .expectErrorMatches(throwable ->
                        throwable instanceof DomainExceptions.TipoPrestamoNoExiste &&
                                throwable.getMessage().equals("Tipo de préstamo no encontrado"))
                .verify();

        verify(repository, times(1)).findById(99L);
    }
}
