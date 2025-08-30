package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.r2dbc.entity.ApplicationEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.ApplicationMapper;
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

/**
 * Test unitario para ApplicationRepositoryAdapter.
 * Incluye transacciones reactivas (TransactionalOperator).
 */
class applicationRepositoryAdapterTest {

    private IApplicationReactiveRepository reactiveRepository; // Repo reactivo
    private ApplicationMapper mapper;                           // Mapper Dominio ↔ Entidad
    private TransactionalOperator transactionalOperator;     // Control transacciones
    private ApplicationRepositoryAdapter adapter;               // Adapter bajo prueba

    @BeforeEach
    void setup() {
        reactiveRepository = Mockito.mock(IApplicationReactiveRepository.class);
        mapper = Mockito.mock(ApplicationMapper.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);

        adapter = new ApplicationRepositoryAdapter(reactiveRepository, mapper, transactionalOperator);
    }

    @Test
    void saveSolicitud_Exitoso() {
        Application application = new Application();
        application.setAmount(BigDecimal.valueOf(1000));

        // Configuración mocks
        when(mapper.toEntity(any())).thenReturn(new ApplicationEntity());
        when(mapper.toModel(any())).thenReturn(application);
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new ApplicationEntity()));
        // Mock del operador transaccional: devuelve el Mono original
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.save(application))
                .expectNext(application)
                .verifyComplete();
    }

    @Test
    void findAllSolicitudes_Exitoso() {
        Application application = new Application();
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new ApplicationEntity()));
        when(mapper.toModel(any())).thenReturn(application);

        StepVerifier.create(adapter.findAll())
                .expectNext(application)
                .verifyComplete();
    }

    @Test
    void findById_Exitoso() {
        Application application = new Application();
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new ApplicationEntity()));
        when(mapper.toModel(any())).thenReturn(application);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(application)
                .verifyComplete();
    }

    @Test
    void delete_Exitoso() {
        // Delete devuelve Mono vacío
        when(reactiveRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.delete(1L))
                .verifyComplete();
    }
}
