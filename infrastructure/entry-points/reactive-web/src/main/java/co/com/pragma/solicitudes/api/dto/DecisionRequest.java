package co.com.pragma.solicitudes.api.dto;

import lombok.Data;

/**
 * Payload de entrada para aprobar/rechazar una solicitud.
 */
@Data
public class DecisionRequest {

    // Id de la solicitud a decidir (obligatorio).
    private Long idApplication;

    // Debe ser "APROBADA" o "RECHAZADA" (coincide con tu enum CodeState).
    private String decision;

    // Observaciones del asesor (opcional).
    private String observations;
}
