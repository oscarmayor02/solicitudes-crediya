package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.r2dbc.entity.EstadoEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.EstadoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstadoRepositoryAdapterTest {

    private IEstadoReactiveRepository reactiveRepository;
    private EstadoMapper mapper;
    private EstadoRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        reactiveRepository = Mockito.mock(IEstadoReactiveRepository.class);
        mapper = Mockito.mock(EstadoMapper.class);
        adapter = new EstadoRepositoryAdapter(reactiveRepository, mapper);
    }

    @Test
    void saveEstado_Exitoso() {
        Estado estado = new Estado();
        when(mapper.toEntity(any())).thenReturn(new EstadoEntity());
        when(mapper.toModel(any())).thenReturn(estado);
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new EstadoEntity()));

        StepVerifier.create(adapter.save(estado))
                .expectNext(estado)
                .verifyComplete();
    }

    @Test
    void findAllEstados_Exitoso() {
        Estado estado = new Estado();
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new EstadoEntity()));
        when(mapper.toModel(any())).thenReturn(estado);

        StepVerifier.create(adapter.findAll())
                .expectNext(estado)
                .verifyComplete();
    }

    @Test
    void findById_Exitoso() {
        Estado estado = new Estado();
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new EstadoEntity()));
        when(mapper.toModel(any())).thenReturn(estado);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(estado)
                .verifyComplete();
    }
}
