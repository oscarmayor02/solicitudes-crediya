package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.r2dbc.entity.ApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
@Repository
public interface IApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationEntity> {
    /**
     * Busca todas las solicitudes de un cliente específico por su documento de identidad.
     */
    Flux<ApplicationEntity> findByIdUser(String documentoCliente);

    /**
     * Busca todas las solicitudes por estado (ejemplo: PENDIENTE, APROBADA, RECHAZADA).
     */
    Flux<ApplicationEntity> findByIdState(String idEstado);

    // Busca solicitudes por un conjunto de estados
    Flux<ApplicationEntity> findByIdStateIn(List<Long> estados);
}
