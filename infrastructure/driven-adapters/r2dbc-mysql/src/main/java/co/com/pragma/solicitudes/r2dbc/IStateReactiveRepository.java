package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.r2dbc.entity.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Repositorio reactivo para StateEntity usando Spring Data R2DBC
 * ReactiveCrudRepository: CRUD básico con Mono/Flux
 * ReactiveQueryByExampleExecutor: permite consultas dinámicas
 */
public interface IStateReactiveRepository extends ReactiveCrudRepository<StateEntity, Long>,
        ReactiveQueryByExampleExecutor<StateEntity> {
}
