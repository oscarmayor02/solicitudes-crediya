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

/**
 * Test unitario para SolicitudUseCase.
 * Validamos creación de solicitudes, errores de monto y tipos de préstamo inexistentes.
 */
class SolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private TipoPrestamoRepository tipoPrestamoRepository;
    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        // Mockear repositorios
        solicitudRepository = Mockito.mock(SolicitudRepository.class);
        tipoPrestamoRepository = Mockito.mock(TipoPrestamoRepository.class);
        // Inyectar mocks en el use case
        solicitudUseCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void crearSolicitud_Valida() {
        // Creamos una solicitud válida
        Solicitud solicitud = Solicitud.builder()
                .monto(BigDecimal.valueOf(5000))
                .plazo(12)
                .email("test@mail.com")
                .idTipoPrestamo(1L)
                .build();

        // Creamos un tipo de préstamo válido
        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .idTipoPrestamo(1L)
                .montoMinimo(BigDecimal.valueOf(1000))
                .montoMaximo(BigDecimal.valueOf(10000))
                .build();

        // Mockear búsqueda del tipo de préstamo
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(tipoPrestamo));
        // Mockear save para devolver la solicitud guardada
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // StepVerifier verifica el flujo Mono
        StepVerifier.create(solicitudUseCase.ejecutar(solicitud))
                // Verificar que el estado fue asignado
                .expectNextMatches(s -> s.getIdEstado() != null)
                .verifyComplete();
    }

    @Test
    void crearSolicitud_MontoFueraDeRango() {
        // Solicitud con monto menor al mínimo
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

        // Verificar que se lanza excepción de monto fuera de rango
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

        // Retornamos Mono.empty para simular que no existe el tipo de préstamo
        when(tipoPrestamoRepository.findById(99L)).thenReturn(Mono.empty());

        // Verificar que se lanza excepción TipoPrestamoNoExiste
        StepVerifier.create(solicitudUseCase.ejecutar(solicitud))
                .expectErrorMatches(throwable -> throwable instanceof DomainExceptions.TipoPrestamoNoExiste)
                .verify();
    }
}
