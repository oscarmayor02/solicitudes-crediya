package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.model.state.gateways.StateRepository;
import co.com.pragma.solicitudes.r2dbc.entity.StateEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.StateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter que implementa el puerto StateRepository usando R2DBC.
 * Hexagonal: interactúa con la DB, devuelve el modelo de dominio.
 *
 * Usamos Mono y Flux:
 * - Mono: representa 0 o 1 elemento (ej: findById, save)
 * - Flux: representa 0 o más elementos (ej: findAll)
 *
 * Se usa SLF4J para trazabilidad profesional en microservicios.
 */
@Repository
public class StateRepositoryAdapter extends ReactiveAdapterOperations<
        State,              // Modelo de dominio
        StateEntity,        // Entidad de persistencia
        Long,                // Tipo de ID
        IStateReactiveRepository // Repositorio reactivo de Spring Data
        > implements StateRepository {

    // Logger profesional, puede integrarse con CloudWatch, ELK, etc.
    private static final Logger log = LoggerFactory.getLogger(StateRepositoryAdapter.class);

    // Repositorio reactivo generado por Spring Data
    private final IStateReactiveRepository reactiveRepository;

    // Mapper para convertir entre Entidad ↔ Dominio
    private final StateMapper stateMapper;

    // Constructor
    protected StateRepositoryAdapter(IStateReactiveRepository repository,
                                     StateMapper stateMapper) {
        super(repository, null, stateMapper::toModel); // Configura ReactiveAdapterOperations
        this.reactiveRepository = repository;
        this.stateMapper = stateMapper;
    }

    @Override
    public Mono<State> save(State state) {
        // Log de inicio de operación
        log.debug("Guardando State: {}", state);

        // Convertimos el modelo de dominio a entidad, guardamos en DB y mapeamos de vuelta a dominio
        return reactiveRepository
                .save(stateMapper.toEntity(state)) // Dominio → Entidad
                .map(stateMapper::toModel)          // Entidad → Dominio
                .doOnNext(e -> log.info("State guardado con ID: {}", e.getIdState()));
    }

    @Override
    public Flux<State> findAll() {
        log.debug("Listando todos los Estados");

        // Flux se usa para representar múltiples elementos, mapeamos cada entidad a dominio
        return reactiveRepository.findAll()
                .map(stateMapper::toModel)
                .doOnNext(e -> log.info("State listado: {}", e.getIdState()));
    }

    @Override
    public Mono<State> findById(Long id) {
        log.debug("Buscando State con ID: {}", id);

        // Mono: representa máximo 1 elemento
        return reactiveRepository.findById(id)
                .map(stateMapper::toModel) // Convertimos a dominio
                .doOnNext(e -> log.info("State encontrado: {}", e))
                .switchIfEmpty(Mono.defer(() -> {
                    // Trazabilidad si no se encuentra
                    log.warn("State no encontrado con ID: {}", id);
                    return Mono.empty(); // No hay elemento
                }));
    }
}
