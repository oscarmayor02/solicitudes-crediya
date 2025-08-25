package co.com.pragma.solicitudes.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad para la tabla tipo_prestamo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    private Long idTipoPrestamo;        // PK
    private String nombre;              // Nombre del tipo de préstamo
    private BigDecimal montoMinimo;     // Monto mínimo permitido
    private BigDecimal montoMaximo;     // Monto máximo permitido
    private Double tasaInteres;         // Tasa de interés
    private Boolean validacionAutomatica; // Si aplica validación automática
}