package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

// TODO: This file is just an example, you should delete or modify it
@Repository
public interface ISolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {
    /**
     * Busca todas las solicitudes de un cliente específico por su documento de identidad.
     */
    Flux<SolicitudEntity> findByIdUsuario(String documentoCliente);

    /**
     * Busca todas las solicitudes por estado (ejemplo: PENDIENTE, APROBADA, RECHAZADA).
     */
    Flux<SolicitudEntity> findByIdEstado(String idEstado);
}
