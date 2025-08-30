package co.com.pragma.solicitudes.model.state.gateways;

import co.com.pragma.solicitudes.model.state.State;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway (puerto de salida) que define las operaciones disponibles
 * para interactuar con la persistencia de Estados.
 * Se implementa en la capa de infraestructura.
 */
public interface StateRepository {
    Mono<State> save(State state);    // Guardar un State
    Flux<State> findAll();              // Listar todos los estados
    Mono<State> findById(Long id);      // Buscar un estado por su ID
}
