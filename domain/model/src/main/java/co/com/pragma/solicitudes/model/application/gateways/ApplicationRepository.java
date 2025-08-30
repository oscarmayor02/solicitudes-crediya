package co.com.pragma.solicitudes.model.application.gateways;

import co.com.pragma.solicitudes.model.application.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway (puerto de salida) que define las operaciones CRUD
 * para la persistencia de Solicitudes.
 */
public interface ApplicationRepository {
    Mono<Application> save(Application application); // Guardar Application
    Flux<Application> findAll();                 // Listar todas
    Mono<Application> findById(Long id);         // Buscar por ID
    Mono<Void> delete(Long id);                // Eliminar solicitud
    Flux<Application> findByState(List<Long> states); // Nuevo: listar solicitudes filtrando por lista de estados

}
