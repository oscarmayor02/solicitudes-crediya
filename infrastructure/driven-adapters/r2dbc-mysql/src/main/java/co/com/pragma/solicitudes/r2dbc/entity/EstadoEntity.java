package co.com.pragma.solicitudes.r2dbc.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad para la tabla estados.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("estados")
public class EstadoEntity {

    @Id
    private Long idEstado;      // PK (ej: "PENDIENTE_REVISION")
    private String nombre;        // Nombre del estado
    private String descripcion;   // Descripción del estado
}
