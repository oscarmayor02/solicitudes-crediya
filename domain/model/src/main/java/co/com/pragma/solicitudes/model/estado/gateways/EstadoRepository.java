package co.com.pragma.solicitudes.model.estado.gateways;

import co.com.pragma.solicitudes.model.estado.Estado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway (puerto de salida) que define las operaciones disponibles
 * para interactuar con la persistencia de Estados.
 * Se implementa en la capa de infraestructura.
 */
public interface EstadoRepository {
    Mono<Estado> save(Estado estado);    // Guardar un estado
    Flux<Estado> findAll();              // Listar todos los estados
    Mono<Estado> findById(Long id);      // Buscar un estado por su ID
}
