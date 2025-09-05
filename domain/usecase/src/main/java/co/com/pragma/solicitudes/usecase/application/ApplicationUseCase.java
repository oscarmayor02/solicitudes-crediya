package co.com.pragma.solicitudes.usecase.application;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.applicationdecisionevent.ApplicationDecisionEvent;
import co.com.pragma.solicitudes.model.applicationdecisionevent.gateways.DecisionPublisher;
import co.com.pragma.solicitudes.model.capacity.CapacityRequestEvent;
import co.com.pragma.solicitudes.model.capacity.CapacityResultEvent;
import co.com.pragma.solicitudes.model.capacity.gateways.ValidationPublisher;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.enums.CodeState;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.reportevent.gateways.ReportsPublisher;
import co.com.pragma.solicitudes.model.user.User;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.UUID;

@Log
@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository usuarioClient;
    private final DecisionPublisher decisionPublisher;
    private final ValidationPublisher validationPublisher;
    private final ReportsPublisher reportsPublisher;

    /**
     * Crea una solicitud. Si el préstamo tiene validación automática,
     * se envía a la Lambda de capacidad de endeudamiento.
     */
    public Mono<Application> execute(Application application, String token) {
        log.info(ApplicationConstants.LOG_START_CREATION + application.getEmail());

        if (application.getAmount() == null || application.getTerm() == null || application.getEmail() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(ApplicationConstants.MSG_MANDATORY_FIELDS));
        }
        if (application.getLoanTypeID() == null) {
            return Mono.error(new DomainExceptions.DatosObligatorios(ApplicationConstants.MSG_LOAN_TYPE_REQUIRED));
        }

        return usuarioClient.getUserById(application.getIdUser(), token)
                .flatMap((User user) ->
                        usuarioClient.existsByEmail(application.getEmail(), token)
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return Mono.error(new DomainExceptions.ValidationException(
                                                ApplicationConstants.MSG_EMAIL_NOT_FOUND));
                                    }
                                    return loanTypeRepository.findById(application.getLoanTypeID())
                                            .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste(
                                                    ApplicationConstants.MSG_LOAN_TYPE_NOT_FOUND)))
                                            .flatMap((LoanType tipoPrestamo) -> validateAmount(application, tipoPrestamo)
                                                    .flatMap(validada -> {
                                                        // Estado inicial
                                                        validada.setIdState(CodeState.PENDIENTE_REVISION.getId());
                                                        return applicationRepository.save(validada)
                                                                .flatMap(saved -> {
                                                                    // ✅ Enviar a Lambda si es validación automática
                                                                    if (Boolean.TRUE.equals(tipoPrestamo.getAutomaticValidation())
                                                                            && !isFinalState(saved.getIdState())) {

                                                                        // Normaliza tasa mensual (si viene como 2.5, se divide entre 100)
                                                                        BigDecimal monthlyRate = tipoPrestamo.getRateInterest();
                                                                        if (monthlyRate != null && monthlyRate.compareTo(BigDecimal.ONE) >= 0) {
                                                                            monthlyRate = monthlyRate.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                                                                        }

                                                                        // Construye el evento para Lambda
                                                                        CapacityRequestEvent event = CapacityRequestEvent.builder()
                                                                                .eventId(UUID.randomUUID().toString())
                                                                                .correlationId(UUID.randomUUID().toString())
                                                                                .idApplication(saved.getIdApplication())
                                                                                .idUser(saved.getIdUser())
                                                                                .email(saved.getEmail())
                                                                                .loanTypeId(saved.getLoanTypeID())
                                                                                .amount(saved.getAmount())
                                                                                .term(saved.getTerm())
                                                                                .monthlyRate(monthlyRate)
                                                                                .deudaMensualActual(BigDecimal.ZERO) // TODO: reemplazar con deuda real
                                                                                .userBaseSalary(user.getBaseSalary())
                                                                                .build();

                                                                        log.info("AUTO-VAL enviar a SQS idApp=" + saved.getIdApplication());
                                                                        return validationPublisher.publish(event).thenReturn(saved);
                                                                    }
                                                                    return Mono.just(saved);
                                                                });
                                                    }));
                                })
                )
                .doOnSuccess(s -> log.info(ApplicationConstants.LOG_APP_CREATED + s.getIdApplication()))
                .doOnError(e -> log.warning(ApplicationConstants.LOG_CREATION_ERROR + e.getMessage()));
    }

    /**
     * Valida que el monto esté dentro del rango permitido.
     */
    private Mono<Application> validateAmount(Application application, LoanType loanType) {
        if (application.getAmount().compareTo(loanType.getMinimumAmount()) < 0 ||
                application.getAmount().compareTo(loanType.getMaximumAmount()) > 0) {
            return Mono.error(new DomainExceptions.MontoFueraDeRango(ApplicationConstants.MSG_AMOUNT_OUT_OF_RANGE));
        }
        return Mono.just(application);
    }

    /**
     * Verifica si el estado es final (APROBADA o RECHAZADA).
     */
    private boolean isFinalState(Long idState) {
        return CodeState.APROBADA.getId().equals(idState) || CodeState.RECHAZADA.getId().equals(idState);
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
     * Aplica decisión manual (APROBADA/RECHAZADA).
     * ✅ Bloquea si la solicitud ya está finalizada.
     */
    public Mono<Application> decide(Long applicationId,
                                    CodeState decision,
                                    String token,
                                    String correlationId,
                                    String observations) {

        if (!(CodeState.APROBADA.equals(decision) || CodeState.RECHAZADA.equals(decision))) {
            return Mono.error(new DomainExceptions.ValidationException(ApplicationConstants.MSG_DECISION_ALLOWED));
        }

        return applicationRepository.findById(applicationId)
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        ApplicationConstants.MSG_APPLICATION_NOT_FOUND)))
                .flatMap(app -> {
                    if (isFinalState(app.getIdState())) {
                        return Mono.error(new DomainExceptions.ValidationException(
                                "La solicitud ya fue procesada y no puede modificarse."));
                    }
                    return Mono.zip(
                            Mono.just(app),
                            usuarioClient.getUserById(app.getIdUser(), token),
                            loanTypeRepository.findById(app.getLoanTypeID())
                    );
                })
                .flatMap(tuple -> {
                    Application app = tuple.getT1();
                    app.setIdState(decision.getId());

                    return applicationRepository.save(app)
                            .flatMap(saved -> {
                                ApplicationDecisionEvent event = ApplicationDecisionEvent.builder()
                                        .eventId(UUID.randomUUID().toString())
                                        .idApplication(saved.getIdApplication())
                                        .idUser(saved.getIdUser())
                                        .email(saved.getEmail())
                                        .loanTypeId(saved.getLoanTypeID())
                                        .decision(decision.name())
                                        .observations(observations)
                                        .correlationId(correlationId)
                                        .decidedAt(java.time.Instant.now())
                                        .build();

                                // Publicar SIEMPRE a la cola de decisiones que ya manejas:
                                Mono<Void> notifyMono = decisionPublisher.publish(event);
// Publicar a REPORTES SOLO si quedó APROBADA:
                                Mono<Void> reportMono = CodeState.APROBADA.equals(decision)
                                        ? reportsPublisher.publishApproved(
                                        String.valueOf(saved.getIdApplication()),
                                        saved.getEmail(),
                                        saved.getAmount(),
                                        saved.getTerm()
                                )
                                        : Mono.empty();

                                return notifyMono.then(reportMono).thenReturn(saved);

                            });
                })
                .doOnSuccess(a -> log.info(ApplicationConstants.LOG_DECISION_APPLY +
                        a.getIdApplication() + decision.name() + correlationId))
                .doOnError(e -> log.warning(ApplicationConstants.LOG_DECISION_ERROR +
                        applicationId + correlationId + e.toString()));
    }

    /**
     * Aplica decisión automática proveniente de la Lambda.
     *  Ignora si ya estaba finalizada.
     */
    public Mono<Application> applyAutoDecision(CapacityResultEvent event) {
        final CodeState state;
        try {
            state = CodeState.valueOf(event.getDecision());
        } catch (IllegalArgumentException ex) {
            return Mono.error(new DomainExceptions.ValidationException(
                    "Decisión automática inválida: " + event.getDecision()));
        }

        return applicationRepository.findById(event.getIdApplication())
                .switchIfEmpty(Mono.error(new DomainExceptions.NotFound(
                        "Solicitud no encontrada: " + event.getIdApplication())))
                .flatMap(app -> {
                    if (isFinalState(app.getIdState())) {
                        return Mono.error(new DomainExceptions.ValidationException(
                                "Solicitud ya finalizada. Se ignora la respuesta automática."));
                    }
                    app.setIdState(state.getId());
                    return applicationRepository.save(app);
                })
                .flatMap(saved -> {
                    if (CodeState.APROBADA.equals(state)) {
                        return reportsPublisher.publishApproved(
                                String.valueOf(saved.getIdApplication()),
                                saved.getEmail(),
                                saved.getAmount(),
                                saved.getTerm()
                        ).thenReturn(saved);
                    }
                    return Mono.just(saved);
                });
    }
}
