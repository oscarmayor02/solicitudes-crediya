package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.application.gateways.ApplicationRepository;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.r2dbc.entity.ApplicationEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.ApplicationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class ApplicationRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        Long,
        IApplicationReactiveRepository
        > implements ApplicationRepository {

    private static final Logger log = LoggerFactory.getLogger(ApplicationRepositoryAdapter.class);

    private final IApplicationReactiveRepository reactiveRepository;
    private final ApplicationMapper applicationMapper;
    private final TransactionalOperator transactionalOperator;

    public ApplicationRepositoryAdapter(IApplicationReactiveRepository repository,
                                        ApplicationMapper applicationMapper,
                                        TransactionalOperator transactionalOperator) {
        super(repository, null, applicationMapper::toModel);
        this.reactiveRepository = repository;
        this.applicationMapper = applicationMapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Application> save(Application application) {
        log.debug(ApplicationConstants.LOG_SAVE_APPLICATION, application);

        return Mono.defer(() -> reactiveRepository
                        .save(applicationMapper.toEntity(application))
                        .map(applicationMapper::toModel))
                .as(transactionalOperator::transactional)
                .doOnNext(s -> log.info(ApplicationConstants.LOG_APPLICATION_SAVE, s.getIdApplication()));
    }

    @Override
    public Flux<Application> findAll() {
        log.debug(ApplicationConstants.LOG_LIST_ALL);

        return reactiveRepository.findAll()
                .map(applicationMapper::toModel)
                .doOnNext(s -> log.info(ApplicationConstants.LOG_APPLICATION_LIST, s.getIdApplication()));
    }

    @Override
    public Mono<Application> findById(Long id) {
        log.debug(ApplicationConstants.LOG_FOUND_BY_ID, id);

        return reactiveRepository.findById(id)
                .map(applicationMapper::toModel)
                .doOnNext(s -> log.info(ApplicationConstants.LOG_APPLICATION_ENCONTRADA, s))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn(ApplicationConstants.LOG_APPLICATION_NO_ENCONTRADA, id);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug(ApplicationConstants.LOG_ELIMINANDO_POR_ID, id);

        return reactiveRepository.deleteById(id)
                .as(transactionalOperator::transactional)
                .doOnSuccess(v -> log.info(ApplicationConstants.LOG_APPLICATION_ELIMINADA, id));
    }

    @Override
    public Flux<Application> findByState(List<Long> estados) {
        log.debug(ApplicationConstants.LOG_LISTANDO_POR_ESTADOS, estados);
        return reactiveRepository.findByIdStateIn(estados)
                .map(applicationMapper::toModel)
                .doOnNext(s -> log.info(ApplicationConstants.LOG_APPLICATION_PARA_REVISION, s.getIdApplication()));
    }
}
