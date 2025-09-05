package co.com.pragma.solicitudes.model.reportevent.gateways;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

/**
 * Puerto de salida para publicar eventos de "aprobado" hacia el micro REPORTES.
 * Mantiene la arquitectura hexagonal separando dominio de infraestructura.
 */
public interface ReportsPublisher {
    Mono<Void> publishApproved(String loanId, String email, BigDecimal amount, Integer term);
}