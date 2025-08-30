package co.com.pragma.solicitudes.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long idApplication;
    private BigDecimal amount;        // monto
    private Integer term;             // plazo
    private String email;             // email
    private String name;            // nombre del solicitante (viene del micro de usuarios)
    private String loanType;      // nombre tipo préstamo
    private BigDecimal rateInterest;   // tasa de interés
    private String stateApplication;   // nombre del estado
    private BigDecimal baseSalary;   // salario del usuario (micro usuarios)
    private BigDecimal TotalMonthlyDebtApprovedRequests; // deuda total
}
