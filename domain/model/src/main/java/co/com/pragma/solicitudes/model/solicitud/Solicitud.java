package co.com.pragma.solicitudes.model.solicitud;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad del dominio que representa una Solicitud de Préstamo.
 * Contiene toda la información necesaria de la petición de crédito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    private Long idSolicitud;      // Identificador único de la solicitud
    private BigDecimal monto;      // Monto solicitado
    private Integer plazo;         // Plazo en meses
    private String email;          // Email del solicitante
    private Long idUsuario;        // FK al usuario en el micro de autenticación
    private Long idEstado;         // FK al estado actual de la solicitud
    private Long idTipoPrestamo;   // FK al tipo de préstamo seleccionado
}
