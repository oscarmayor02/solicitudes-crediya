package co.com.pragma.solicitudes.usecase.solicitud;

import co.com.pragma.solicitudes.model.enums.EstadoCodigo;
import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;

import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Caso de uso para crear una nueva Solicitud de préstamo.
 * Contiene las reglas de negocio y validaciones.
 */
@Log
@RequiredArgsConstructor
public class SolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;

    /**
     * Crear solicitud
     */
    public Mono<Solicitud> ejecutar(Solicitud solicitud) {

        log.info("Iniciando creación de solicitud para usuario: {}" + solicitud.getEmail());

        // Validaciones básicas
        if (solicitud.getMonto() == null || solicitud.getPlazo() == null || solicitud.getEmail() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios("Monto, plazo y email son obligatorios"));
        }

        if (solicitud.getIdTipoPrestamo() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios("Debe indicar el tipo de préstamo"));
        }

        // Validar tipo de préstamo
        // Operación dependiente: validar tipo de préstamo existe
        return tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo()) // devuelve Mono<TipoPrestamo>
                .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste(
                        "Tipo de préstamo no encontrado"))) // si no existe, retorna un Mono de error
                .flatMap(tipoPrestamo -> validarMonto(solicitud, tipoPrestamo))
                // flatMap -> encadena validaciones asíncronas, retorna Mono<Solicitud>
                .flatMap(validada -> {
                    validada.setIdEstado(EstadoCodigo.PENDIENTE_REVISION.getId());
                    return solicitudRepository.save(validada); // guarda y devuelve Mono<Solicitud>
                })
                .doOnSuccess(s -> log.info("Solicitud creada con ID: {}" + s.getIdSolicitud()))
                // doOnSuccess -> logging cuando se completa con éxito
                .doOnError(e -> log.warning("Error al crear solicitud: {}" + e.getMessage()));
        // doOnError -> logging si ocurre error
    }

    // Validación de monto
    private Mono<Solicitud> validarMonto(Solicitud solicitud, TipoPrestamo tipoPrestamo) {
        if (solicitud.getMonto().compareTo(tipoPrestamo.getMontoMinimo()) < 0 ||
                solicitud.getMonto().compareTo(tipoPrestamo.getMontoMaximo()) > 0) {
            return Mono.error(new DomainExceptions.MontoFueraDeRango("Monto fuera del rango permitido para este tipo de préstamo"));
        }
        return Mono.just(solicitud);
    }

    public Flux<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }
// Flux = colección reactiva de 0..N elementos

    public Mono<Solicitud> getSolicitudById(Long id) {
        return solicitudRepository.findById(id)
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound("Solicitud no encontrada")));
    }
// switchIfEmpty -> retorna error si no existe el registro

    public Mono<Solicitud> editSolicitud(Solicitud solicitud) {
        return solicitudRepository.findById(solicitud.getIdSolicitud())
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound("Solicitud no encontrada para editar")))
                .flatMap(existing -> solicitudRepository.save(solicitud));
    }

    public Mono<Void> delete(Long id) {
        return solicitudRepository.delete(id);
    }


}
