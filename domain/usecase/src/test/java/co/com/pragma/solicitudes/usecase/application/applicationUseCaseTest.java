package co.com.pragma.solicitudes.usecase.application;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.model.applicationdecisionevent.gateways.DecisionPublisher;
import co.com.pragma.solicitudes.model.capacity.gateways.ValidationPublisher;
import co.com.pragma.solicitudes.model.enums.CodeState;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.user.User;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApplicationUseCaseTest {

    private ApplicationRepository applicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private UserRepository usuarioClient;
    private DecisionPublisher decisionPublisher;
    private ApplicationUseCase useCase;
    private  ValidationPublisher validationPublisher; // puerto para publicar validación

    @BeforeEach
    public void setUp() {
        applicationRepository = Mockito.mock(ApplicationRepository.class);
        loanTypeRepository    = Mockito.mock(LoanTypeRepository.class);
        usuarioClient         = Mockito.mock(UserRepository.class);
        decisionPublisher     = Mockito.mock(DecisionPublisher.class);
        validationPublisher     = Mockito.mock(ValidationPublisher.class);

        useCase = new ApplicationUseCase(
                applicationRepository, loanTypeRepository, usuarioClient, decisionPublisher, validationPublisher);

        // Publicación a SQS siempre OK
        when(decisionPublisher.publish(any())).thenReturn(Mono.empty());
    }

    @Test
    void crearSolicitud_Valida() {
        Application input = Application.builder()
                .amount(BigDecimal.valueOf(5_000))
                .term(12)
                .email("test@mail.com")
                .loanTypeID(1L)
                .idUser(1L)
                .build();

        LoanType loanType = LoanType.builder()
                .loanTypeID(1L)
                .minimumAmount(BigDecimal.valueOf(1_000))
                .maximumAmount(BigDecimal.valueOf(10_000))
                .build();

        // Devuelve un User válido (mock o real)
        User mockUser = User.builder()
                .idNumber(1L)
                .name("Test")
                .lastName("User")
                .email("test@mail.com")
                .baseSalary(BigDecimal.valueOf(3_000_000))
                .build();

        when(usuarioClient.getUserById(eq(1L), anyString())).thenReturn(Mono.just(mockUser));
        when(usuarioClient.existsByEmail(eq("test@mail.com"), anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.execute(input, "token"))
                .assertNext(saved -> org.junit.jupiter.api.Assertions.assertEquals(
                        CodeState.PENDIENTE_REVISION.getId(), saved.getIdState()))
                .verifyComplete();
    }

    @Test
    void crearSolicitud_MontoFueraDeRango() {
        Application input = Application.builder()
                .amount(BigDecimal.valueOf(500)) // menor al mínimo
                .term(12)
                .email("test@mail.com")
                .loanTypeID(1L)
                .idUser(1L)
                .build();

        LoanType loanType = LoanType.builder()
                .loanTypeID(1L)
                .minimumAmount(BigDecimal.valueOf(1_000))
                .maximumAmount(BigDecimal.valueOf(10_000))
                .build();

        User mockUser = User.builder().idNumber(1L).name("T").lastName("U").email("test@mail.com").build();

        when(usuarioClient.getUserById(eq(1L), anyString())).thenReturn(Mono.just(mockUser));
        when(usuarioClient.existsByEmail(eq("test@mail.com"), anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.execute(input, "token"))
                .expectError(DomainExceptions.MontoFueraDeRango.class)
                .verify();
    }

    @Test
    void crearSolicitud_TipoPrestamoNoExiste() {
        Application input = Application.builder()
                .amount(BigDecimal.valueOf(5_000))
                .term(12)
                .email("test@mail.com")
                .loanTypeID(99L)
                .idUser(1L)
                .build();

        User mockUser = User.builder().idNumber(1L).name("T").lastName("U").email("test@mail.com").build();

        when(usuarioClient.getUserById(eq(1L), anyString())).thenReturn(Mono.just(mockUser));
        when(usuarioClient.existsByEmail(eq("test@mail.com"), anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(input, "token"))
                .expectError(DomainExceptions.TipoPrestamoNoExiste.class)
                .verify();
    }

    @Test
    void decidir_Aprobada_PublicaEvento() {
        Application existing = Application.builder()
                .idApplication(10L)
                .email("user@mail.com")
                .idUser(1L)
                .loanTypeID(2L)
                .idState(CodeState.PENDIENTE_REVISION.getId())
                .build();

        User mockUser = User.builder().idNumber(1L).name("T").lastName("U").email("user@mail.com").build();
        LoanType loanType = LoanType.builder().loanTypeID(2L).build();

        when(applicationRepository.findById(10L)).thenReturn(Mono.just(existing));
        when(usuarioClient.getUserById(eq(1L), anyString())).thenReturn(Mono.just(mockUser));
        when(loanTypeRepository.findById(2L)).thenReturn(Mono.just(loanType));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.decide(10L, CodeState.APROBADA, "token", "corr-1", "obs"))
                .assertNext(saved -> org.junit.jupiter.api.Assertions.assertEquals(
                        CodeState.APROBADA.getId(), saved.getIdState()))
                .verifyComplete();

        // se publicó el evento
        verify(decisionPublisher, times(1)).publish(any());
    }

    @Test
    void decidir_Invalida_LanzaValidacion() {
        StepVerifier.create(useCase.decide(10L, CodeState.PENDIENTE_REVISION, "t", "c", "o"))
                .expectError(DomainExceptions.ValidationException.class)
                .verify();
    }
}
