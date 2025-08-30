package co.com.pragma.solicitudes.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad para la tabla tipo_prestamo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("loan_type")
public class LoanTypeEntity {

    @Id
    @Column("loan_type_id")
    private Long loanTypeID;

    @Column("name")
    private String name;

    @Column("minimumAmount")
    private BigDecimal minimumAmount;

    @Column("maximumAmount")
    private BigDecimal maximumAmount;

    @Column("rateInterest")
    private BigDecimal rateInterest;

    @Column("automaticValidation")
    private Boolean automaticValidation;
}