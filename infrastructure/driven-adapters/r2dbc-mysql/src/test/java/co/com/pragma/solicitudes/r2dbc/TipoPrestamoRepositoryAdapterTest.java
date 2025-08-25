package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.TipoPrestamoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TipoPrestamoRepositoryAdapterTest {

    private ITipoPrestamoReactiveRepository reactiveRepository;
    private TipoPrestamoMapper mapper;
    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        reactiveRepository = Mockito.mock(ITipoPrestamoReactiveRepository.class);
        mapper = Mockito.mock(TipoPrestamoMapper.class);
        adapter = new TipoPrestamoRepositoryAdapter(reactiveRepository, mapper);
    }

    @Test
    void saveTipoPrestamo_Exitoso() {
        TipoPrestamo tipo = new TipoPrestamo();
        when(mapper.toEntity(any())).thenReturn(new TipoPrestamoEntity());
        when(mapper.toModel(any())).thenReturn(tipo);
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new TipoPrestamoEntity()));

        StepVerifier.create(adapter.save(tipo))
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void findAllTiposPrestamo_Exitoso() {
        TipoPrestamo tipo = new TipoPrestamo();
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new TipoPrestamoEntity()));
        when(mapper.toModel(any())).thenReturn(tipo);

        StepVerifier.create(adapter.findAll())
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void findById_Exitoso() {
        TipoPrestamo tipo = new TipoPrestamo();
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new TipoPrestamoEntity()));
        when(mapper.toModel(any())).thenReturn(tipo);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void existsByNombreIgnoreCase_Exitoso() {
        when(reactiveRepository.existsByNombreIgnoreCase("Personal")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNombreIgnoreCase("Personal"))
                .expectNext(true)
                .verifyComplete();
    }
}
