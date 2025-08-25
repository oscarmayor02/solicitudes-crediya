package co.com.pragma.solicitudes.usecase.solicitud;

import co.com.pragma.solicitudes.model.enums.EstadoCodigo;
import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear una nueva Solicitud de préstamo.
 * Contiene las reglas de negocio y validaciones.
 * Se integra con micro de autenticación para validar usuarios.
 */
@Log
@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final UserRepository usuarioClient; // Cliente REST para consultar usuarios

    /**
     * Crear solicitud
     */
    public Mono<Solicitud> ejecutar(Solicitud solicitud) {

        // Log inicial con email recibido
        log.info("Iniciando creación de solicitud para usuario: {}" + solicitud.getEmail());

        // Validaciones básicas: monto, plazo y email son obligatorios
        if (solicitud.getMonto() == null || solicitud.getPlazo() == null || solicitud.getEmail() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(
                    "Monto, plazo y email son obligatorios"));
        }

        // Validación de tipo de préstamo obligatorio
        if (solicitud.getIdTipoPrestamo() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(
                    "Debe indicar el tipo de préstamo"));
        }

        // Consultamos el usuario en el micro de autenticación por ID
        return usuarioClient.getUserById(solicitud.getIdUsuario())
                .flatMap(user -> {
                    log.info("Usuario encontrado: {}" + user.toString());

                    // Ahora validamos el email usando existsByEmail para evitar NPE
                    return usuarioClient.existsByEmail(solicitud.getEmail())
                            .flatMap(exists -> {
                                log.info("Email existe en sistema: {}" + exists);

                                if (!exists) {
                                    // Si el email no existe, se lanza excepción de validación
                                    return Mono.error(new DomainExceptions.ValidationException(
                                            "El email proporcionado no existe en el sistema"));
                                }

                                // Validación del tipo de préstamo
                                return tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo())
                                        .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste(
                                                "Tipo de préstamo no encontrado")))
                                        // Validación de monto dentro del rango permitido
                                        .flatMap(tipoPrestamo -> validarMonto(solicitud, tipoPrestamo))
                                        // Si todo es correcto, seteamos estado y guardamos
                                        .flatMap(validada -> {
                                            validada.setIdEstado(EstadoCodigo.PENDIENTE_REVISION.getId());
                                            return solicitudRepository.save(validada);
                                        });
                            });
                })
                // Log de éxito
                .doOnSuccess(s -> log.info("Solicitud creada con ID: {}" + s.getIdSolicitud()))
                // Log de error
                .doOnError(e -> log.warning("Error al crear solicitud: {}" + e.getMessage()));
    }

    /**
     * Validación de monto: debe estar dentro del rango permitido del tipo de préstamo
     */
    private Mono<Solicitud> validarMonto(Solicitud solicitud, TipoPrestamo tipoPrestamo) {
        if (solicitud.getMonto().compareTo(tipoPrestamo.getMontoMinimo()) < 0 ||
                solicitud.getMonto().compareTo(tipoPrestamo.getMontoMaximo()) > 0) {
            return Mono.error(new DomainExceptions.MontoFueraDeRango(
                    "Monto fuera del rango permitido para este tipo de préstamo"));
        }
        return Mono.just(solicitud);
    }

    /**
     * Obtener todas las solicitudes
     */
    public Flux<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    /**
     * Obtener solicitud por ID
     */
    public Mono<Solicitud> getSolicitudById(Long id) {
        return solicitudRepository.findById(id)
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        "Solicitud no encontrada")));
    }

    /**
     * Editar solicitud existente
     */
    public Mono<Solicitud> editSolicitud(Solicitud solicitud) {
        return solicitudRepository.findById(solicitud.getIdSolicitud())
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        "Solicitud no encontrada para editar")))
                .flatMap(existing -> solicitudRepository.save(solicitud));
    }

    /**
     * Eliminar solicitud por ID
     */
    public Mono<Void> delete(Long id) {
        return solicitudRepository.delete(id);
    }
}
