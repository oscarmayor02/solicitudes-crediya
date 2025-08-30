package co.com.pragma.solicitudes.usecase.state;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.model.state.gateways.StateRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para la gestión de Estados.
 * Orquesta la lógica de negocio usando el repositorio.
 */
@RequiredArgsConstructor
public class StateUseCase {

    private final StateRepository stateRepository;

    /**
     * Crea un State nuevo.
     * Devuelve un Mono porque es una operación asíncrona y reactiva.
     * Mono = 0 o 1 elemento.
     */
    public Mono<State> createState(State state) {
        if (state.getName() == null || state.getDescription() == null) {
            // Mono.error genera un flujo que termina en error reactivo si faltan datos obligatorios
            return Mono.error(new DomainExceptions.DatosObligatorios(ApplicationConstants.ERROR_NAME_DESCRIPTION_REQUIRED));
        }
        // Guarda el State usando el repositorio reactivo
        return stateRepository.save(state);
    }

    /**
     * Lista todos los estados.
     * Flux = 0..N elementos (colección reactiva)
     */
    public Flux<State> listState() {
        return stateRepository.findAll(); // No bloquea, la base de datos puede emitir varios registros
    }
}

