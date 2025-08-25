package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.SolicitudMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudRepositoryAdapterTest {

    private ISolicitudReactiveRepository reactiveRepository;
    private SolicitudMapper mapper;
    private TransactionalOperator transactionalOperator;
    private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        reactiveRepository = Mockito.mock(ISolicitudReactiveRepository.class);
        mapper = Mockito.mock(SolicitudMapper.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);

        adapter = new SolicitudRepositoryAdapter(reactiveRepository, mapper, transactionalOperator);
    }

    @Test
    void saveSolicitud_Exitoso() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(BigDecimal.valueOf(1000));

        when(mapper.toEntity(any())).thenReturn(new SolicitudEntity());
        when(mapper.toModel(any())).thenReturn(solicitud);
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new SolicitudEntity()));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.save(solicitud))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void findAllSolicitudes_Exitoso() {
        Solicitud solicitud = new Solicitud();
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new SolicitudEntity()));
        when(mapper.toModel(any())).thenReturn(solicitud);

        StepVerifier.create(adapter.findAll())
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void findById_Exitoso() {
        Solicitud solicitud = new Solicitud();
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new SolicitudEntity()));
        when(mapper.toModel(any())).thenReturn(solicitud);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void delete_Exitoso() {
        when(reactiveRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.delete(1L))
                .verifyComplete();
    }
}
