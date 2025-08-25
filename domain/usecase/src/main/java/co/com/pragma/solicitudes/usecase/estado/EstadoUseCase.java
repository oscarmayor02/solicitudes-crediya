package co.com.pragma.solicitudes.usecase.estado;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.model.estado.gateways.EstadoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para la gestión de Estados.
 * Orquesta la lógica de negocio usando el repositorio.
 */
@RequiredArgsConstructor
public class EstadoUseCase {

    private final EstadoRepository repository; // Puerto hacia infraestructura

    // Crear nuevo estado
    public Mono<Estado> createEstado(Estado estado) {
        return repository.save(estado); // Guardar estado en BD
    }

    // Listar estados disponibles
    public Flux<Estado> listEstados() {
        return repository.findAll(); // Listar todos los estados
    }
}
