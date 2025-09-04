package co.com.pragma.solicitudes.r2dbc.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad para la tabla estados.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("state")
public class StateEntity {

    @Id
    @Column("id_state")
    private Long idState;      // PK (ej: "PENDIENTE_REVISION")
    private String name;        // Nombre del estado
    private String description;   // Descripción del estado
}
