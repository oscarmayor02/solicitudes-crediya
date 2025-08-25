package co.com.pragma.solicitudes.model.enums;

/**
 * Enum que define los estados posibles de una Solicitud.
 * Se usa para evitar "códigos mágicos" en el código y darles un significado claro.
 */
public enum EstadoCodigo {
    PENDIENTE_REVISION(1L),   // Estado inicial de una solicitud recién creada
    APROBADO(2L),             // Estado cuando la solicitud fue aceptada
    RECHAZADO(3L);            // Estado cuando la solicitud fue denegada

    private final Long id;    // Identificador que se usará en BD como FK

    // Constructor privado que asigna el ID al enum
    EstadoCodigo(Long id) {
        this.id = id;
    }

    // Getter para obtener el ID del enum
    public Long getId() {
        return id;
    }
}
