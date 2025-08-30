package co.com.pragma.solicitudes.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad para la tabla solicitud en la base de datos relacional.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("application")
public class ApplicationEntity {

    @Id
    @Column("id_application")
    private Long idApplication;      // Identificador único de la solicitud
    private BigDecimal amount;      // Monto solicitado
    private Integer term;         // Plazo en meses
    private String email;          // Email del solicitante
    @Column("id_user")
    private Long idUser;        // FK al usuario en el micro de autenticación
    @Column("id_state")
    private Long idState;         // FK al estado actual de la solicitud
    @Column("loan_type_id")
    private Long loanTypeID;   // FK al tipo de préstamo seleccionado
}