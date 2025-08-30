package co.com.pragma.solicitudes.model.loantype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo de dominio que representa un Tipo de Préstamo.
 * Ej: "Libre inversión", "Educación", "Vivienda".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanType {
    private Long loanTypeID;      // Identificador único del tipo de préstamo
    private String name;            // Nombre del tipo de préstamo
    private BigDecimal minimumAmount;   // Monto mínimo permitido
    private BigDecimal maximumAmount;   // Monto máximo permitido
    private BigDecimal rateInterest;   // Tasa de interés aplicada
    private Boolean automaticValidation; // Si el préstamo se aprueba automáticamente
}
