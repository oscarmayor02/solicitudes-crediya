package co.com.pragma.solicitudes.usecase.tipoprestamo;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Caso de uso para la gestión de Tipos de Préstamo.
 */
/**
 * Caso de uso para crear, listar y obtener tipos de préstamo.
 */
@RequiredArgsConstructor
public class TipoPrestamoUseCase {
    private final TipoPrestamoRepository repository;

    // Crear un nuevo tipo de préstamo
    public Mono<TipoPrestamo> crear(TipoPrestamo tipo) {
        validar(tipo);
        return repository.existsByNombreIgnoreCase(tipo.getNombre())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DomainExceptions.DatosObligatorios("Ya existe un tipo de préstamo con ese nombre"));
                    }
                    return repository.save(tipo);
                });
    }

    // Listar todos los tipos
    public Flux<TipoPrestamo> listar() {
        return repository.findAll();
    }

    // Obtener por ID
    public Mono<TipoPrestamo> obtenerPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste("Tipo de préstamo no encontrado")));
    }

    // Validaciones de negocio
    private void validar(TipoPrestamo tipo) {
        if (tipo.getNombre() == null || tipo.getNombre().isBlank()) {
            throw new DomainExceptions.DatosObligatorios("El nombre es obligatorio");
        }
        if (tipo.getMontoMinimo() == null || tipo.getMontoMaximo() == null) {
            throw new DomainExceptions.DatosObligatorios("Montos mínimo y máximo son obligatorios");
        }
        if (tipo.getMontoMinimo().compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainExceptions.MontoFueraDeRango("El monto mínimo no puede ser negativo");
        }
        if (tipo.getMontoMaximo().compareTo(tipo.getMontoMinimo()) < 0) {
            throw new DomainExceptions.MontoFueraDeRango("El monto máximo debe ser >= monto mínimo");
        }
        if (tipo.getTasaInteres() == null || tipo.getTasaInteres().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainExceptions.DatosObligatorios("La tasa de interés debe ser > 0");
        }
        if (tipo.getValidacionAutomatica() == null) {
            tipo.setValidacionAutomatica(Boolean.FALSE);
        }
    }
}