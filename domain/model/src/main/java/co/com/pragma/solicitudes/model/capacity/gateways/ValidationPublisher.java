package co.com.pragma.solicitudes.model.capacity.gateways;

import co.com.pragma.solicitudes.model.capacity.CapacityRequestEvent;
import reactor.core.publisher.Mono;

public interface ValidationPublisher {
    Mono<Void> publish(CapacityRequestEvent event);
}