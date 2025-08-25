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

/**
 * Test unitario para EstadoRepositoryAdapter.
 * Verifica que las operaciones básicas (save, findAll, findById) funcionen correctamente.
 */
class EstadoRepositoryAdapterTest {

    private IEstadoReactiveRepository reactiveRepository; // Mock del repositorio reactivo
    private EstadoMapper mapper;                           // Mock del mapper Dominio ↔ Entidad
    private EstadoRepositoryAdapter adapter;               // Adapter que vamos a probar

    @BeforeEach
    void setup() {
        // Creamos mocks
        reactiveRepository = Mockito.mock(IEstadoReactiveRepository.class);
        mapper = Mockito.mock(EstadoMapper.class);

        // Inicializamos el adapter con los mocks
        adapter = new EstadoRepositoryAdapter(reactiveRepository, mapper);
    }

    @Test
    void saveEstado_Exitoso() {
        Estado estado = new Estado(); // Dominio que queremos guardar

        // Configuramos el comportamiento de los mocks
        when(mapper.toEntity(any())).thenReturn(new EstadoEntity()); // Mapper: dominio → entidad
        when(mapper.toModel(any())).thenReturn(estado);             // Mapper: entidad → dominio
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new EstadoEntity())); // Repositorio reactivo devuelve Mono<Entidad>

        // StepVerifier verifica el comportamiento reactivo
        StepVerifier.create(adapter.save(estado)) // Invocamos el método save del adapter
                .expectNext(estado)               // Esperamos recibir el objeto dominio
                .verifyComplete();               // Verificamos que el flujo se complete correctamente
    }

    @Test
    void findAllEstados_Exitoso() {
        Estado estado = new Estado();

        // Configuramos los mocks
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new EstadoEntity())); // Repositorio devuelve Flux<Entidad>
        when(mapper.toModel(any())).thenReturn(estado); // Mapper convierte a dominio

        StepVerifier.create(adapter.findAll())   // Probamos el método findAll
                .expectNext(estado)             // Verificamos que devuelva el objeto dominio
                .verifyComplete();              // Verificamos que el flujo se complete
    }

    @Test
    void findById_Exitoso() {
        Estado estado = new Estado();

        // Mock del repositorio y del mapper
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new EstadoEntity()));
        when(mapper.toModel(any())).thenReturn(estado);

        StepVerifier.create(adapter.findById(1L)) // Probamos findById
                .expectNext(estado)               // Verificamos que retorne el objeto dominio
                .verifyComplete();
    }
}
