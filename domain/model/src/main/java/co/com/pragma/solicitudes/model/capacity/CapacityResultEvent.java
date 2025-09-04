package co.com.pragma.solicitudes.model.capacity;


import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CapacityResultEvent {
    private String eventId;
    private String correlationId;
    private Long idApplication;
    private String decision; // APROBADA | REVISION_MANUAL | RECHAZADA
    private String observations;
    private BigDecimal capacidadMaxima;
    private BigDecimal deudaMensualActual;
    private BigDecimal capacidadDisponible;
    private BigDecimal cuotaPrestamoNuevo;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Cuota {
        private Integer n;
        private BigDecimal capital;
        private BigDecimal interes;
        private BigDecimal saldo;
    }
    private List<Cuota> planPago;
}