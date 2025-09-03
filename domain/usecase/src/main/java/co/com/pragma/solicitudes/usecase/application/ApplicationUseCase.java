package co.com.pragma.solicitudes.usecase.application;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.applicationdecisionevent.ApplicationDecisionEvent;
import co.com.pragma.solicitudes.model.applicationdecisionevent.gateways.DecisionPublisher;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.enums.CodeState;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Log
@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository usuarioClient;
    private final DecisionPublisher decisionPublisher; // nuevo puerto

    public Mono<Application> execute(Application application, String token) {
        log.info(ApplicationConstants.LOG_START_CREATION + application.getEmail());

        if (application.getAmount() == null || application.getTerm() == null || application.getEmail() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(
                    ApplicationConstants.MSG_MANDATORY_FIELDS));
        }

        if (application.getLoanTypeID() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(
                    ApplicationConstants.MSG_LOAN_TYPE_REQUIRED));
        }

        return usuarioClient.getUserById(application.getIdUser(), token)
                .flatMap(user -> {
                    log.info(ApplicationConstants.LOG_USER_FOUND_OK + user);

                    return usuarioClient.existsByEmail(application.getEmail(), token)
                            .flatMap(exists -> {
                                log.info(ApplicationConstants.LOG_EMAIL_EXISTS + exists);

                                if (!exists) {
                                    return Mono.error(new DomainExceptions.ValidationException(
                                            ApplicationConstants.MSG_EMAIL_NOT_FOUND));
                                }

                                return loanTypeRepository.findById(application.getLoanTypeID())
                                        .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste(
                                                ApplicationConstants.MSG_LOAN_TYPE_NOT_FOUND)))
                                        .flatMap(tipoPrestamo -> validateAmount(application, tipoPrestamo))
                                        .flatMap(validada -> {
                                            validada.setIdState(CodeState.PENDIENTE_REVISION.getId());
                                            return applicationRepository.save(validada);
                                        });
                            });
                })
                .doOnSuccess(s -> log.info(ApplicationConstants.LOG_APP_CREATED + s.getIdApplication()))
                .doOnError(e -> log.warning(ApplicationConstants.LOG_CREATION_ERROR + e.getMessage()));
    }

    private Mono<Application> validateAmount(Application application, LoanType loanType) {
        if (application.getAmount().compareTo(loanType.getMinimumAmount()) < 0 ||
                application.getAmount().compareTo(loanType.getMaximumAmount()) > 0) {
            return Mono.error(new DomainExceptions.MontoFueraDeRango(
                    ApplicationConstants.MSG_AMOUNT_OUT_OF_RANGE));
        }
        return Mono.just(application);
    }

    public Flux<Application> getAllApplication() {
        return applicationRepository.findAll();
    }

    public Mono<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        ApplicationConstants.MSG_APPLICATION_NOT_FOUND)));
    }

    public Mono<Application> editApplication(Application application) {
        return applicationRepository.findById(application.getIdApplication())
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        ApplicationConstants.MSG_APPLICATION_NOT_FOUND_EDIT)))
                .flatMap(existing -> applicationRepository.save(application));
    }

    public Flux<Application> getApplicationForReview() {
        List<Long> estadosRevision = List.of(
                CodeState.PENDIENTE_REVISION.getId(),
                CodeState.RECHAZADA.getId(),
                CodeState.REVISION_MANUAL.getId()
        );
        return applicationRepository.findByState(estadosRevision);
    }

    public Mono<Void> delete(Long id) {
        return applicationRepository.delete(id);
    }

    public Flux<Application> getApplicationsByState(Long idEstado) {
        return applicationRepository.findByState(List.of(idEstado));
    }

    /**
     * Aplica decisión final (APROBADA/RECHAZADA) y publica evento a SQS.
     */
    public Mono<Application> decide(Long applicationId,
                                    CodeState decision,
                                    String token,
                                    String correlationId,
                                    String observations) {

        // Valida que la decisión sea una de las permitidas.
        if (!(CodeState.APROBADA.equals(decision) || CodeState.RECHAZADA.equals(decision))) {
            return Mono.error(new DomainExceptions.ValidationException(
                    ApplicationConstants.MSG_DECISION_ALLOWED));
        }

        return applicationRepository.findById(applicationId)
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        ApplicationConstants.MSG_APPLICATION_NOT_FOUND)))
                // Enriquecemos con user y loanType (útil para notificación)
                .flatMap(app -> Mono.zip(
                        Mono.just(app),
                        usuarioClient.getUserById(app.getIdUser(), token),
                        loanTypeRepository.findById(app.getLoanTypeID())
                ))
                .flatMap(tuple -> {
                    Application app = tuple.getT1();

                    // Cambiamos el estado en memoria.
                    app.setIdState(decision.getId());

                    // Persistimos y luego publicamos el evento.
                    return applicationRepository.save(app)
                            .flatMap(saved -> {
                                ApplicationDecisionEvent event = ApplicationDecisionEvent.builder()
                                        .eventId(java.util.UUID.randomUUID().toString())
                                        .idApplication(saved.getIdApplication())
                                        .idUser(saved.getIdUser())
                                        .email(saved.getEmail())
                                        .loanTypeId(saved.getLoanTypeID())
                                        .decision(decision.name())
                                        .observations(observations)
                                        .correlationId(correlationId)
                                        .decidedAt(java.time.Instant.now())
                                        .build();

                                return decisionPublisher.publish(event).thenReturn(saved);
                            });
                })
                .doOnSuccess(a -> log.info(ApplicationConstants.LOG_DECISION_APPLY +
                        a.getIdApplication()+ decision.name()+ correlationId))
                .doOnError(e -> log.warning(ApplicationConstants.LOG_DECISION_ERROR+
                        applicationId+ correlationId+ e.toString()));
    }


}
