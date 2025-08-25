package co.com.pragma.solicitudes.model.tipoprestamo.gateways;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway (puerto de salida) para interactuar con la persistencia de Tipos de Préstamo.
 */
public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> save(TipoPrestamo tipo);                  // Guardar
    Mono<TipoPrestamo> findById(Long id);                        // Buscar por ID
    Flux<TipoPrestamo> findAll();                                // Listar todos
    Mono<Boolean> existsById(Long id);                           // Validar existencia por ID
    Mono<Boolean> existsByNombreIgnoreCase(String nombre);       // Validar nombre único//
 }
