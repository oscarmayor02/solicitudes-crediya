package co.com.pragma.solicitudes.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad para la tabla solicitud en la base de datos relacional.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("solicitud")
public class SolicitudEntity {

    @Id
    private Long idSolicitud;        // PK
    private BigDecimal monto;        // Monto solicitado
    private Integer plazo;           // Plazo en meses
    private String email;            // Email del solicitante
    private Long idTipoPrestamo;     // FK hacia tipo_prestamo
    private Long idEstado;         // Estado actual
    private Long idUsuario;          // Id del usuario (del micro autenticación)
}