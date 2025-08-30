package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.LoanTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Long,
        ILoanTypeReactiveRepository
        > implements LoanTypeRepository {

    private static final Logger log = LoggerFactory.getLogger(LoanTypeRepositoryAdapter.class);

    private final LoanTypeMapper loanTypeMapper;
    private final ILoanTypeReactiveRepository reactiveRepository;

    public LoanTypeRepositoryAdapter(ILoanTypeReactiveRepository repository,
                                     LoanTypeMapper loanTypeMapper) {
        super(repository, null, loanTypeMapper::toModel);
        this.loanTypeMapper = loanTypeMapper;
        this.reactiveRepository = repository;
    }

    @Override
    public Mono<LoanType> save(LoanType tipo) {
        log.debug(ApplicationConstants.LOG_SAVE_LOANTYPE, tipo);

        return reactiveRepository.save(loanTypeMapper.toEntity(tipo))
                .map(loanTypeMapper::toModel)
                .doOnNext(t -> log.info(ApplicationConstants.LOG_LOANTYPE_SAVE, t.getLoanTypeID()));
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        log.debug(ApplicationConstants.LOG_FOUND_BY_ID, id);

        return reactiveRepository.findById(id)
                .map(loanTypeMapper::toModel)
                .doOnNext(t -> log.info(ApplicationConstants.LOG_LOANTYPE_FOUND, t))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn(ApplicationConstants.LOG_LOANTYPE_NO_FOUND, id);
                    return Mono.empty();
                }));
    }

    @Override
    public Flux<LoanType> findAll() {
        log.debug(ApplicationConstants.LOG_LIST_LOAN);

        return reactiveRepository.findAll()
                .map(loanTypeMapper::toModel)
                .doOnNext(t -> log.info(ApplicationConstants.LOG_LOANTYPE_LIST, t.getName()));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        log.debug(ApplicationConstants.LOG_VERIFICate_EXISTEN_BY_ID, id);
        return reactiveRepository.existsById(id)
                .doOnNext(exists -> log.info(ApplicationConstants.LOG_EXIST_NY_ID, id, exists));
    }

    @Override
    public Mono<Boolean> existsByNameIgnoreCase(String nombre) {
        log.debug(ApplicationConstants.LOG_VERIFICATE_EXISTEN_BY_NAME, nombre);
        return reactiveRepository.existsByNameIgnoreCase(nombre)
                .doOnNext(exists -> log.info(ApplicationConstants.LOG_EXIST_BY_NAME, nombre, exists));
    }
}
