package co.com.pragma.solicitudes.model.application;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad del dominio que representa una Application de Préstamo.
 * Contiene toda la información necesaria de la petición de crédito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Long idApplication;      // Identificador único de la solicitud
    private BigDecimal amount;      // Monto solicitado
    private Integer term;         // Plazo en meses
    private String email;          // Email del solicitante
    private Long idUser;        // FK al usuario en el micro de autenticación
    private Long idState;         // FK al estado actual de la solicitud
    private Long loanTypeID;   // FK al tipo de préstamo seleccionado
}
