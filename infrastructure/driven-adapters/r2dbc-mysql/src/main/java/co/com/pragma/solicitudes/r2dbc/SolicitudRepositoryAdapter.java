package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.SolicitudMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter de Solicitud siguiendo Hexagonal y microservicio.
 * Garantiza atomicidad con TransactionalOperator.
 */
@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        ISolicitudReactiveRepository
        > implements SolicitudRepository {

    private static final Logger log = LoggerFactory.getLogger(SolicitudRepositoryAdapter.class);

    private final ISolicitudReactiveRepository reactiveRepository;
    private final SolicitudMapper solicitudMapper;
    private final TransactionalOperator transactionalOperator;

    public SolicitudRepositoryAdapter(ISolicitudReactiveRepository repository,
                                      SolicitudMapper solicitudMapper,
                                      TransactionalOperator transactionalOperator) {
        super(repository, null, solicitudMapper::toModel);
        this.reactiveRepository = repository;
        this.solicitudMapper = solicitudMapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        log.debug("Guardando Solicitud: {}", solicitud);

        return Mono.defer(() -> reactiveRepository
                        .save(solicitudMapper.toEntity(solicitud)) // Dominio → Entidad
                        .map(solicitudMapper::toModel))            // Entidad → Dominio
                .as(transactionalOperator::transactional)          // Atomicidad reactiva
                .doOnNext(s -> log.info("Solicitud guardada con ID: {}", s.getIdSolicitud()));
    }

    @Override
    public Flux<Solicitud> findAll() {
        log.debug("Listando todas las Solicitudes");

        return reactiveRepository.findAll()
                .map(solicitudMapper::toModel)
                .doOnNext(s -> log.info("Solicitud listada: {}", s.getIdSolicitud()));
    }

    @Override
    public Mono<Solicitud> findById(Long id) {
        log.debug("Buscando Solicitud con ID: {}", id);

        return reactiveRepository.findById(id)
                .map(solicitudMapper::toModel)
                .doOnNext(s -> log.info("Solicitud encontrada: {}", s))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Solicitud no encontrada con ID: {}", id);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Eliminando Solicitud con ID: {}", id);

        return reactiveRepository.deleteById(id)
                .as(transactionalOperator::transactional)
                .doOnSuccess(v -> log.info("Solicitud eliminada con ID: {}", id));
    }
}
