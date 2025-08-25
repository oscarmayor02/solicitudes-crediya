package co.com.pragma.solicitudes.usecase.solicitud;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private TipoPrestamoRepository tipoPrestamoRepository;
    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = Mockito.mock(SolicitudRepository.class);
        tipoPrestamoRepository = Mockito.mock(TipoPrestamoRepository.class);
        solicitudUseCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void crearSolicitud_Valida() {
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(5000))
                .plazo(12)
                .email("test@mail.com")
                .idTipoPrestamo(1L)
                .build();

        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .idTipoPrestamo(1L)
                .montoMinimo(BigDecimal.valueOf(1000))
                .montoMaximo(BigDecimal.valueOf(10000))
                .build();

        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(solicitudUseCase.ejecutar(solicitud))
                .expectNextMatches(s -> s.getIdEstado() != null)
                .verifyComplete();
    }

    @Test
    void crearSolicitud_MontoFueraDeRango() {
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(500))
                .plazo(12)
                .email("test@mail.com")
                .idTipoPrestamo(1L)
                .build();

        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .idTipoPrestamo(1L)
                .montoMinimo(BigDecimal.valueOf(1000))
                .montoMaximo(BigDecimal.valueOf(10000))
                .build();

        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(solicitudUseCase.ejecutar(solicitud))
                .expectErrorMatches(throwable -> throwable instanceof DomainExceptions.MontoFueraDeRango)
                .verify();
    }

    @Test
    void crearSolicitud_TipoPrestamoNoExiste() {
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(5000))
                .plazo(12)
                .email("test@mail.com")
                .idTipoPrestamo(99L)
                .build();

        when(tipoPrestamoRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.ejecutar(solicitud))
                .expectErrorMatches(throwable -> throwable instanceof DomainExceptions.TipoPrestamoNoExiste)
                .verify();
    }
}
