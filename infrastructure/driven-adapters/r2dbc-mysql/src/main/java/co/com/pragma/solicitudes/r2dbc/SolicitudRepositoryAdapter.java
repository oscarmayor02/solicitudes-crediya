package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.SolicitudMapper;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.SolicitudMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter que implementa el puerto SolicitudRepository con R2DBC.
 * Se asegura atomicidad con TransactionalOperator en operaciones de escritura.
 */
@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,                 // Modelo de dominio
        SolicitudEntity,           // Entidad de infraestructura
        Long,                      // Tipo de ID
        ISolicitudReactiveRepository // Repositorio reactivo
        > implements SolicitudRepository {

    private final ISolicitudReactiveRepository reactiveRepository;
    private final SolicitudMapper solicitudMapper;
    private final TransactionalOperator transactionalOperator;

    public SolicitudRepositoryAdapter(ISolicitudReactiveRepository repository,
                                      SolicitudMapper solicitudMapper,
                                      TransactionalOperator transactionalOperator) {
        // El mapeo Entity → Domain se delega al mapper
        super(repository, null, solicitudMapper::toModel);
        this.reactiveRepository = repository;
        this.solicitudMapper = solicitudMapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        // Guardar con transacción reactiva para garantizar atomicidad
        return Mono.defer(() -> reactiveRepository
                        .save(solicitudMapper.toEntity(solicitud)) // Dominio → Entidad
                        .map(solicitudMapper::toModel))            // Entidad → Dominio
                .as(transactionalOperator::transactional);
    }

    @Override
    public Flux<Solicitud> findAll() {
        // Listar todas las solicitudes mapeadas a dominio
        return reactiveRepository.findAll()
                .map(solicitudMapper::toModel);
    }

    @Override
    public Mono<Solicitud> findById(Long id) {
        // Buscar por ID y mapear a dominio
        return reactiveRepository.findById(id)
                .map(solicitudMapper::toModel);
        // Si quieres forzar error cuando no exista:
        // .switchIfEmpty(Mono.error(new DomainExceptions.NotFound("Solicitud no encontrada")));
    }

    @Override
    public Mono<Void> delete(Long id) {
        // Eliminar por ID dentro de transacción
        return reactiveRepository.deleteById(id)
                .as(transactionalOperator::transactional);
    }
}
