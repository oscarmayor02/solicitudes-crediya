package co.com.pragma.solicitudes.usecase.loanType;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Caso de uso para crear, listar y obtener tipos de préstamo.
 */
@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeRepository repository;

    // Crear un nuevo tipo de préstamo
    public Mono<LoanType> create(LoanType type) {
        validate(type);
        return repository.existsByNameIgnoreCase(type.getName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DomainExceptions.DatosObligatorios(
                                ApplicationConstants.NAME_ALREADY_EXISTS
                        ));
                    }
                    return repository.save(type);
                });
    }

    // Listar todos los tipos
    public Flux<LoanType> listLoanType() {
        return repository.findAll();
    }

    // Obtener por ID
    public Mono<LoanType> getById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DomainExceptions.TipoPrestamoNoExiste(
                        ApplicationConstants.LOAN_TYPE_NOT_FOUND
                )));
    }

    // Validaciones de negocio
    private void validate(LoanType type) {

        if (type.getName() == null || type.getName().isBlank()) {
            throw new DomainExceptions.DatosObligatorios(ApplicationConstants.NAME_REQUIRED);
        }
        if (type.getMinimumAmount() == null || type.getMaximumAmount() == null) {
            throw new DomainExceptions.DatosObligatorios(ApplicationConstants.AMOUNTS_REQUIRED);
        }
        if (type.getMinimumAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainExceptions.MontoFueraDeRango(ApplicationConstants.MIN_AMOUNT_NEGATIVE);
        }
        if (type.getMaximumAmount().compareTo(type.getMinimumAmount()) < 0) {
            throw new DomainExceptions.MontoFueraDeRango(ApplicationConstants.MAX_AMOUNT_INVALID);
        }
        if (type.getRateInterest() == null || type.getRateInterest().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainExceptions.DatosObligatorios(ApplicationConstants.INTEREST_REQUIRED);
        }
        if (type.getAutomaticValidation() == null) {
            type.setAutomaticValidation(Boolean.FALSE);
        }
    }
}
