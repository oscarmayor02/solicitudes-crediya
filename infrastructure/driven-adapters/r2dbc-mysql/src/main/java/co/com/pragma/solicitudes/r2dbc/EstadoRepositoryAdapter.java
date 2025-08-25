package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.model.estado.gateways.EstadoRepository;
import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.solicitudes.r2dbc.entity.EstadoEntity;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.EstadoMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
@Repository
public class EstadoRepositoryAdapter  extends ReactiveAdapterOperations<
        Estado/* change for domain model */,
        EstadoEntity/* change for adapter model */,
        Long,
        IEstadoReactiveRepository
        > implements EstadoRepository {


    private final IEstadoReactiveRepository reactiveRepository;
    private final EstadoMapper estadoMapper;

    protected EstadoRepositoryAdapter(IEstadoReactiveRepository repository,
                                      EstadoMapper estadoMapper) {
        super(repository, null, estadoMapper::toModel);{
                this.reactiveRepository = repository;
                this.estadoMapper = estadoMapper;
            }
    }


    @Override
    public Mono<Estado> save(Estado estado) {
        return reactiveRepository.save(estadoMapper.toEntity(estado))
                .map(estadoMapper::toModel);
    }

    @Override
    public Flux<Estado> findAll() {
        return reactiveRepository.findAll()
                .map(estadoMapper::toModel);
    }

    @Override
    public Mono<Estado> findById(Long id) {
        return reactiveRepository.findById(id)
                .map(estadoMapper::toModel);
    }
}

