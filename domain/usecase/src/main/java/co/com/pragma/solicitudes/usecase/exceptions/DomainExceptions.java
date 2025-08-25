package co.com.pragma.solicitudes.usecase.exceptions;

/**
 * Excepciones de dominio personalizadas.
 * Permiten diferenciar errores de negocio en el flujo de solicitudes.
 */
public class DomainExceptions extends RuntimeException{
    private final String code;

    public DomainExceptions(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // Todas las subclases ahora heredan de DomainExceptions
    public static class NotFound extends DomainExceptions {
        public NotFound(String message) {
            super("NOT_FOUND", message);
        }
    }

    public static class TipoPrestamoNoExiste extends DomainExceptions {
        public TipoPrestamoNoExiste(String msg) {
            super("TIPO_PRESTAMO_NO_EXISTE", msg);
        }
    }

    public static class MontoFueraDeRango extends DomainExceptions {
        public MontoFueraDeRango(String msg) {
            super("MONTO_FUERA_DE_RANGO", msg);
        }
    }

    public static class PlazoInvalido extends DomainExceptions {
        public PlazoInvalido(String msg) {
            super("PLAZO_INVALIDO", msg);
        }
    }

    public static class DatosObligatorios extends DomainExceptions {
        public DatosObligatorios(String msg) {
            super("DATOS_OBLIGATORIOS", msg);
        }
    }
}
