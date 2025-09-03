package co.com.pragma.solicitudes.model.applicationdecisionevent.gateways;
import co.com.pragma.solicitudes.model.applicationdecisionevent.ApplicationDecisionEvent;
import reactor.core.publisher.Mono;

/**
 * Puerto hexagonal para publicar eventos de decisión.
 * Implementado por un adaptador SQS.
 */
public interface DecisionPublisher {

    /**
     * Publica el evento a la infraestructura de mensajería.
     * @param event evento de decisión.
     * @return Mono que completa al finalizar el envío.
     */
    Mono<Void> publish(ApplicationDecisionEvent event);
}