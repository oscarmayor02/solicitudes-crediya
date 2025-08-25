package co.com.pragma.solicitudes.r2dbc;


import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.TipoPrestamoMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Repository;

/**
 * Adaptador para el repositorio de TipoPrestamo usando R2DBC y ReactiveAdapterOperations.
 */
@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo, // Dominio
        TipoPrestamoEntity, // Entidad
        Long, // ID
        ITipoPrestamoReactiveRepository // Repositorio reactivo
        > implements TipoPrestamoRepository {

    private final TipoPrestamoMapper tipoPrestamoMapper;
    private final ITipoPrestamoReactiveRepository reactiveRepository;

    public TipoPrestamoRepositoryAdapter(ITipoPrestamoReactiveRepository repository,
                                         TipoPrestamoMapper tipoPrestamoMapper) {
        /**
         * ReactiveAdapterOperations hace la conversión automática usando el mapper
         */
        super(repository, null, tipoPrestamoMapper::toModel);
        this.tipoPrestamoMapper = tipoPrestamoMapper;
        this.reactiveRepository = repository;
    }

    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo tipo) {
        return reactiveRepository.save(tipoPrestamoMapper.toEntity(tipo))
                .map(tipoPrestamoMapper::toModel);
    }

    @Override
    public Mono<TipoPrestamo> findById(Long id) {
        return reactiveRepository.findById(id)
                .map(tipoPrestamoMapper::toModel);
    }

    @Override
    public Flux<TipoPrestamo> findAll() {
        return reactiveRepository.findAll()
                .map(tipoPrestamoMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return reactiveRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByNombreIgnoreCase(String nombre) {
        return reactiveRepository.existsByNombreIgnoreCase(nombre);
    }
}

