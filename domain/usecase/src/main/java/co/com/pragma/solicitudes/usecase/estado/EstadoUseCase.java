package co.com.pragma.solicitudes.usecase.estado;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.model.estado.gateways.EstadoRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para la gestión de Estados.
 * Orquesta la lógica de negocio usando el repositorio.
 */
@RequiredArgsConstructor
public class EstadoUseCase {

    private final EstadoRepository estadoRepository;

    /**
     * Crea un estado nuevo.
     * Devuelve un Mono porque es una operación asíncrona y reactiva.
     * Mono = 0 o 1 elemento.
     */
    public Mono<Estado> createEstado(Estado estado) {
        if (estado.getNombre() == null || estado.getDescripcion() == null) {
            // Mono.error genera un flujo que termina en error reactivo si faltan datos obligatorios
            return Mono.error(new DomainExceptions.DatosObligatorios("Nombre y descripción son obligatorios"));
        }
        // Guarda el estado usando el repositorio reactivo
        return estadoRepository.save(estado);
    }

    /**
     * Lista todos los estados.
     * Flux = 0..N elementos (colección reactiva)
     */
    public Flux<Estado> listEstados() {
        return estadoRepository.findAll(); // No bloquea, la base de datos puede emitir varios registros
    }
}

