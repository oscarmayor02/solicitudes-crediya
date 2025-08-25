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

    public Mono<Estado> createEstado(Estado estado) {
        if (estado.getNombre() == null || estado.getDescripcion() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios("Nombre y descripción son obligatorios"));
        }
        return estadoRepository.save(estado);
    }

    public Flux<Estado> listEstados() {
        return estadoRepository.findAll();
    }
}

