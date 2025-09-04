package co.com.pragma.solicitudes.api.web;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
/**
 * Filtro WebFlux que garantiza que cada request tenga un "X-Correlation-Id".
 * - Si el cliente lo envía, lo reutiliza.
 * - Si no, genera uno nuevo (UUID).
 * Luego:
 * - Lo agrega al header de la respuesta (para que el cliente lo vea).
 * - Lo pone en el MDC para que todos los logs incluyan ese ID.
 * - Al finalizar la request, limpia el MDC para evitar fugas entre hilos/reactores.
 */
@Component // Indica a Spring que registre esta clase como componente (bean) y la aplique como filtro global.
public class CorrelationFilter implements WebFilter { // Implementa WebFilter para actuar sobre todas las solicitudes WebFlux.

    @Override // Sobrescribe el método del contrato WebFilter.
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) { // 'exchange' tiene request+response; 'chain' continúa el flujo de filtros/handler.
        ServerHttpRequest req = exchange.getRequest(); // Obtiene la request actual (para inspeccionar headers, path, etc.).

        // Intenta obtener el correlation-id enviado por el cliente en el header definido en ApplicationConstants.HDR_CORRELATION_ID (ej. "X-Correlation-Id").
        String corr = req.getHeaders().getFirst(ApplicationConstants.HDR_CORRELATION_ID);

        // Si el header no viene o viene vacío, generamos un nuevo UUID y lo devolvemos en la respuesta para trazabilidad.
        if (corr == null || corr.isBlank()) { // Verifica null o cadena en blanco.
            corr = java.util.UUID.randomUUID().toString(); // Genera un identificador único (UUID v4) para esta request.
            exchange.getResponse().getHeaders().add(ApplicationConstants.HDR_CORRELATION_ID, corr); // Lo añade en la respuesta (el cliente podrá leerlo).
        }

        // Coloca el correlation-id en el MDC para que aparezca automáticamente en todos los logs que se emitan durante el manejo de esta request.
        MDC.put(ApplicationConstants.HDR_CORRELATION_ID, corr);

        // Continúa con la cadena de filtros / controlador. Al finalizar SIEMPRE se ejecuta 'doFinally' para limpiar el MDC.
        return chain.filter(exchange) // Delega al siguiente filtro/handler manteniendo el contexto reactivo.
                .doFinally(signalType -> MDC.remove(ApplicationConstants.HDR_CORRELATION_ID)); // Limpia el MDC al terminar (éxito, error o cancelación) y evita contaminación entre requests.
    }
}