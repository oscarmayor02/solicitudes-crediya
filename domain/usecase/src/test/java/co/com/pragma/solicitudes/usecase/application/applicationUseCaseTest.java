package co.com.pragma.solicitudes.usecase.application;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test unitario para ApplicationUseCase.
 * Validamos creación de solicitudes, errores de monto y tipos de préstamo inexistentes.
 */
class applicationUseCaseTest {

    private ApplicationRepository applicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private ApplicationUseCase applicationUseCase;

    @BeforeEach
    void setUp() {
        // Mockear repositorios
        applicationRepository = Mockito.mock(ApplicationRepository.class);
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        // Inyectar mocks en el use case
        applicationUseCase = new ApplicationUseCase(applicationRepository, loanTypeRepository, null);
    }

    @Test
    void crearSolicitud_Valida() {
        // Creamos una Application válida
        Application application = application.builder()
                .amount(BigDecimal.valueOf(5000))
                .term(12)
                .email("test@mail.com")
                .loanTypeID(1L)
                .build();

        // Creamos un tipo de préstamo válido
        LoanType loanType = loanType.builder()
                .idTipoPrestamo(1L)
                .montoMinimo(BigDecimal.valueOf(1000))
                .montoMaximo(BigDecimal.valueOf(10000))
                .build();

        // Mockear búsqueda del tipo de préstamo
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        // Mockear save para devolver la Application guardada
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // StepVerifier verifica el flujo Mono
        StepVerifier.create(applicationUseCase.execute(application, "token-fake"))
                // Verificar que el estado fue asignado
                .expectNextMatches(s -> s.getIdState() != null)
                .verifyComplete();
    }

    @Test
    void crearSolicitud_MontoFueraDeRango() {
        // Application con monto menor al mínimo
        Application application = application.builder()
                .amount(BigDecimal.valueOf(500))
                .term(12)
                .email("test@mail.com")
                .loanTypeID(1L)
                .build();

        LoanType loanType = loanType.builder()
                .idTipoPrestamo(1L)
                .montoMinimo(BigDecimal.valueOf(1000))
                .montoMaximo(BigDecimal.valueOf(10000))
                .build();

        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));

        // Verificar que se lanza excepción de monto fuera de rango
        StepVerifier.create(applicationUseCase.execute(application,  "token-fake"))
                .expectErrorMatches(throwable -> throwable instanceof DomainExceptions.MontoFueraDeRango)
                .verify();
    }

    @Test
    void crearSolicitud_TipoPrestamoNoExiste() {
        Application application = application.builder()
                .amount(BigDecimal.valueOf(5000))
                .term(12)
                .email("test@mail.com")
                .loanTypeID(99L)
                .build();

        // Retornamos Mono.empty para simular que no existe el tipo de préstamo
        when(loanTypeRepository.findById(99L)).thenReturn(Mono.empty());

        // Verificar que se lanza excepción TipoPrestamoNoExiste
        StepVerifier.create(applicationUseCase.execute(application, "token-fake"))
                .expectErrorMatches(throwable -> throwable instanceof DomainExceptions.TipoPrestamoNoExiste)
                .verify();
    }
}
