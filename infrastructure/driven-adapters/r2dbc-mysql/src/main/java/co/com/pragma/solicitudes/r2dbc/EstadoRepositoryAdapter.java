package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.model.estado.gateways.EstadoRepository;
import co.com.pragma.solicitudes.r2dbc.entity.EstadoEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.EstadoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter que implementa el puerto EstadoRepository usando R2DBC.
 * Hexagonal: interactúa con la DB, devuelve el modelo de dominio.
 *
 * Usamos Mono y Flux:
 * - Mono: representa 0 o 1 elemento (ej: findById, save)
 * - Flux: representa 0 o más elementos (ej: findAll)
 *
 * Se usa SLF4J para trazabilidad profesional en microservicios.
 */
@Repository
public class EstadoRepositoryAdapter extends ReactiveAdapterOperations<
        Estado,              // Modelo de dominio
        EstadoEntity,        // Entidad de persistencia
        Long,                // Tipo de ID
        IEstadoReactiveRepository // Repositorio reactivo de Spring Data
        > implements EstadoRepository {

    // Logger profesional, puede integrarse con CloudWatch, ELK, etc.
    private static final Logger log = LoggerFactory.getLogger(EstadoRepositoryAdapter.class);

    // Repositorio reactivo generado por Spring Data
    private final IEstadoReactiveRepository reactiveRepository;

    // Mapper para convertir entre Entidad ↔ Dominio
    private final EstadoMapper estadoMapper;

    // Constructor
    protected EstadoRepositoryAdapter(IEstadoReactiveRepository repository,
                                      EstadoMapper estadoMapper) {
        super(repository, null, estadoMapper::toModel); // Configura ReactiveAdapterOperations
        this.reactiveRepository = repository;
        this.estadoMapper = estadoMapper;
    }

    @Override
    public Mono<Estado> save(Estado estado) {
        // Log de inicio de operación
        log.debug("Guardando Estado: {}", estado);

        // Convertimos el modelo de dominio a entidad, guardamos en DB y mapeamos de vuelta a dominio
        return reactiveRepository
                .save(estadoMapper.toEntity(estado)) // Dominio → Entidad
                .map(estadoMapper::toModel)          // Entidad → Dominio
                .doOnNext(e -> log.info("Estado guardado con ID: {}", e.getIdEstado()));
    }

    @Override
    public Flux<Estado> findAll() {
        log.debug("Listando todos los Estados");

        // Flux se usa para representar múltiples elementos, mapeamos cada entidad a dominio
        return reactiveRepository.findAll()
                .map(estadoMapper::toModel)
                .doOnNext(e -> log.info("Estado listado: {}", e.getIdEstado()));
    }

    @Override
    public Mono<Estado> findById(Long id) {
        log.debug("Buscando Estado con ID: {}", id);

        // Mono: representa máximo 1 elemento
        return reactiveRepository.findById(id)
                .map(estadoMapper::toModel) // Convertimos a dominio
                .doOnNext(e -> log.info("Estado encontrado: {}", e))
                .switchIfEmpty(Mono.defer(() -> {
                    // Trazabilidad si no se encuentra
                    log.warn("Estado no encontrado con ID: {}", id);
                    return Mono.empty(); // No hay elemento
                }));
    }
}
