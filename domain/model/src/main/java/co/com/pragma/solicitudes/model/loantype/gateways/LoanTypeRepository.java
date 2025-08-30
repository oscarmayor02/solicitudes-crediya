package co.com.pragma.solicitudes.model.loantype.gateways;

import co.com.pragma.solicitudes.model.loantype.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway (puerto de salida) para interactuar con la persistencia de Tipos de Préstamo.
 */
public interface LoanTypeRepository {
    Mono<LoanType> save(LoanType tipo);                  // Guardar
    Mono<LoanType> findById(Long id);                        // Buscar por ID
    Flux<LoanType> findAll();                                // Listar todos
    Mono<Boolean> existsById(Long id);                           // Validar existencia por ID
    Mono<Boolean> existsByNameIgnoreCase(String name);       // Validar nombre único//
 }
