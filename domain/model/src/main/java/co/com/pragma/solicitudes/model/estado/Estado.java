package co.com.pragma.solicitudes.model.estado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio que representa el estado de una Solicitud.
 * Ej: "Pendiente de revisión", "Aprobado", "Rechazado".
 * NO tiene anotaciones de persistencia, porque el dominio debe ser independiente de la infraestructura.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    private Long idEstado;      // Identificador único del estado (PK en BD)
    private String nombre;      // Nombre del estado (ej: "Pendiente de revisión")
    private String descripcion; // Descripción adicional del estado
}
