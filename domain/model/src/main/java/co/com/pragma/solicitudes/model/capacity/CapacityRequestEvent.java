package co.com.pragma.solicitudes.model.capacity;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CapacityRequestEvent {
    private String eventId;
    private String correlationId;
    private Long idApplication;
    private Long idUser;
    private String email;
    private Long loanTypeId;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyRate;      // tasa mensual (ej. 0.0185)
    private BigDecimal userBaseSalary;   // salario base
    private BigDecimal deudaMensualActual; // suma de cuotas activas (para local => 0)
}