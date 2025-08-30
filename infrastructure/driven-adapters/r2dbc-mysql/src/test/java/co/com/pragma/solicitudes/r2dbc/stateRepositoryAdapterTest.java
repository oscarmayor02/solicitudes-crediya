package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.r2dbc.entity.StateEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.StateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para StateRepositoryAdapter.
 * Verifica que las operaciones básicas (save, findAll, findById) funcionen correctamente.
 */
class stateRepositoryAdapterTest {

    private IStateReactiveRepository reactiveRepository; // Mock del repositorio reactivo
    private StateMapper mapper;                           // Mock del mapper Dominio ↔ Entidad
    private StateRepositoryAdapter adapter;               // Adapter que vamos a probar

    @BeforeEach
    void setup() {
        // Creamos mocks
        reactiveRepository = Mockito.mock(IStateReactiveRepository.class);
        mapper = Mockito.mock(StateMapper.class);

        // Inicializamos el adapter con los mocks
        adapter = new StateRepositoryAdapter(reactiveRepository, mapper);
    }

    @Test
    void saveEstado_Exitoso() {
        State state = new State(); // Dominio que queremos guardar

        // Configuramos el comportamiento de los mocks
        when(mapper.toEntity(any())).thenReturn(new StateEntity()); // Mapper: dominio → entidad
        when(mapper.toModel(any())).thenReturn(state);             // Mapper: entidad → dominio
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new StateEntity())); // Repositorio reactivo devuelve Mono<Entidad>

        // StepVerifier verifica el comportamiento reactivo
        StepVerifier.create(adapter.save(state)) // Invocamos el método save del adapter
                .expectNext(state)               // Esperamos recibir el objeto dominio
                .verifyComplete();               // Verificamos que el flujo se complete correctamente
    }

    @Test
    void findAllEstados_Exitoso() {
        State state = new State();

        // Configuramos los mocks
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new StateEntity())); // Repositorio devuelve Flux<Entidad>
        when(mapper.toModel(any())).thenReturn(state); // Mapper convierte a dominio

        StepVerifier.create(adapter.findAll())   // Probamos el método findAll
                .expectNext(state)             // Verificamos que devuelva el objeto dominio
                .verifyComplete();              // Verificamos que el flujo se complete
    }

    @Test
    void findById_Exitoso() {
        State state = new State();

        // Mock del repositorio y del mapper
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new StateEntity()));
        when(mapper.toModel(any())).thenReturn(state);

        StepVerifier.create(adapter.findById(1L)) // Probamos findById
                .expectNext(state)               // Verificamos que retorne el objeto dominio
                .verifyComplete();
    }
}
