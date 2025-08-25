package co.com.pragma.solicitudes.model.tipoprestamo;

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
public class TipoPrestamo {
    private Long idTipoPrestamo;      // Identificador único del tipo de préstamo
    private String nombre;            // Nombre del tipo de préstamo
    private BigDecimal montoMinimo;   // Monto mínimo permitido
    private BigDecimal montoMaximo;   // Monto máximo permitido
    private BigDecimal tasaInteres;   // Tasa de interés aplicada
    private Boolean validacionAutomatica; // Si el préstamo se aprueba automáticamente
}
