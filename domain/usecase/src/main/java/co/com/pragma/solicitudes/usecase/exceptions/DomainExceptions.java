package co.com.pragma.solicitudes.usecase.exceptions;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;

/**
 * Excepciones de dominio personalizadas para la capa de negocio.
 * Cada excepción tiene un código único y un mensaje asociado.
 */
public abstract class DomainExceptions extends RuntimeException {

    private final String code;

    protected DomainExceptions(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // ===================== EXCEPCIONES ESPECÍFICAS =====================

    /**
     * Recurso no encontrado.
     */
    public static class NotFound extends DomainExceptions {
        public NotFound(String message) {
            super(ApplicationConstants.NOT_FOUND, message);
        }
    }

    /**
     * Tipo de préstamo no existe.
     */
    public static class TipoPrestamoNoExiste extends DomainExceptions {
        public TipoPrestamoNoExiste(String message) {
            super(ApplicationConstants.TIPO_PRESTAMO_NO_EXISTE, message);
        }
    }

    /**
     * Monto fuera de rango permitido.
     */
    public static class MontoFueraDeRango extends DomainExceptions {
        public MontoFueraDeRango(String message) {
            super(ApplicationConstants.MONTO_FUERA_DE_RANGO, message);
        }
    }

    /**
     * Plazo inválido para el préstamo.
     */
    public static class PlazoInvalido extends DomainExceptions {
        public PlazoInvalido(String message) {
            super(ApplicationConstants.PLAZO_INVALIDO, message);
        }
    }

    /**
     * Faltan datos obligatorios.
     */
    public static class DatosObligatorios extends DomainExceptions {
        public DatosObligatorios(String message) {
            super(ApplicationConstants.DATOS_OBLIGATORIOS, message);
        }
    }

    /**
     * Error genérico de validación.
     */
    public static class ValidationException extends DomainExceptions {
        public ValidationException(String message) {
            super(ApplicationConstants.VALIDATION_ERROR, message);
        }
    }
}
