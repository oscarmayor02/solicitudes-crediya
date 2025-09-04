package co.com.pragma.solicitudes.model.applicationdecisionevent;

import lombok.*;
import java.time.Instant;

/**
 * Evento enviado a SQS cuando una solicitud es aprobada o rechazada.
 * Consumido luego por Lambda para notificar (SNS/SES).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDecisionEvent {

    // Id único para deduplicación/idempotencia (especialmente en colas FIFO).
    private String eventId;

    // Identificadores de la solicitud y del usuario.
    private Long idApplication;
    private Long idUser;

    // Email del solicitante (destinatario final).
    private String email;

    // "APROBADA" o "RECHAZADA".
    private String decision;

    // Enriquecimiento (tipo de préstamo) por si la notificación lo requiere.
    private Long loanTypeId;

    // Notas del asesor.
    private String observations;

    // CorrelationId para traza extremo a extremo.
    private String correlationId;

    // Fecha/hora de decisión (UTC).
    private Instant decidedAt;
}