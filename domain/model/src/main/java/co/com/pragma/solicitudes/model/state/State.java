package co.com.pragma.solicitudes.model.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio que representa el estado de una Application.
 * Ej: "Pendiente de revisión", "Aprobado", "Rechazado".
 * NO tiene anotaciones de persistencia, porque el dominio debe ser independiente de la infraestructura.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Long idState;      // Identificador único del estado (PK en BD)
    private String name;      // Nombre del estado (ej: "Pendiente de revisión")
    private String description; // Descripción adicional del estado
}
