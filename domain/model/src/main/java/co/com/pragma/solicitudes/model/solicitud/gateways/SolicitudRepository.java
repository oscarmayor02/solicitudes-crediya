package co.com.pragma.solicitudes.model.solicitud.gateways;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Gateway (puerto de salida) que define las operaciones CRUD
 * para la persistencia de Solicitudes.
 */
public interface SolicitudRepository {
    Mono<Solicitud> save(Solicitud solicitud); // Guardar solicitud
    Flux<Solicitud> findAll();                 // Listar todas
    Mono<Solicitud> findById(Long id);         // Buscar por ID
    Mono<Void> delete(Long id);                // Eliminar solicitud
}
