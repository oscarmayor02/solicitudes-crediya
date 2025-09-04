package co.com.pragma.solicitudes.api.config;

import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.model.applicationdecisionevent.gateways.DecisionPublisher;
import co.com.pragma.solicitudes.model.capacity.gateways.ValidationPublisher;
import co.com.pragma.solicitudes.usecase.application.ApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expone los casos de uso como beans de Spring.
 */
@Configuration
@RequiredArgsConstructor
public class UseCaseConfig {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository userRepository;
    private final DecisionPublisher decisionPublisher;
    private final ValidationPublisher validationPublisher; // ⬅️ nuevo

    @Bean
    public ApplicationUseCase applicationUseCase() {
        // Construye el use case con sus dependencias de dominio/infra
        return new ApplicationUseCase(applicationRepository, loanTypeRepository, userRepository, decisionPublisher,validationPublisher);
    }
}
