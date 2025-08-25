package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.solicitudes.r2dbc.mapper.TipoPrestamoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter para el repositorio de TipoPrestamo usando R2DBC.
 * Implementa el puerto TipoPrestamoRepository (Hexagonal Architecture)
 * usando ReactiveAdapterOperations para simplificar mapeos.
 *
 * Mono: representa 0 o 1 elemento.
 * Flux: representa 0 o más elementos.
 * SLF4J para trazabilidad de microservicio.
 */
@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,           // Dominio
        TipoPrestamoEntity,     // Entidad de persistencia
        Long,                   // Tipo de ID
        ITipoPrestamoReactiveRepository // Repositorio reactivo
        > implements TipoPrestamoRepository {

    // Logger profesional
    private static final Logger log = LoggerFactory.getLogger(TipoPrestamoRepositoryAdapter.class);

    private final TipoPrestamoMapper tipoPrestamoMapper;            // Mapper Dominio ↔ Entidad
    private final ITipoPrestamoReactiveRepository reactiveRepository; // Repositorio reactivo Spring Data

    // Constructor
    public TipoPrestamoRepositoryAdapter(ITipoPrestamoReactiveRepository repository,
                                         TipoPrestamoMapper tipoPrestamoMapper) {
        // Configuramos ReactiveAdapterOperations para manejo automático de conversiones
        super(repository, null, tipoPrestamoMapper::toModel);
        this.tipoPrestamoMapper = tipoPrestamoMapper;
        this.reactiveRepository = repository;
    }

    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo tipo) {
        log.debug("Guardando TipoPrestamo: {}", tipo);

        // Guardar en la DB y mapear a dominio
        return reactiveRepository.save(tipoPrestamoMapper.toEntity(tipo)) // Dominio → Entidad
                .map(tipoPrestamoMapper::toModel)                          // Entidad → Dominio
                .doOnNext(t -> log.info("TipoPrestamo guardado con ID: {}", t.getIdTipoPrestamo()));
    }

    @Override
    public Mono<TipoPrestamo> findById(Long id) {
        log.debug("Buscando TipoPrestamo con ID: {}", id);

        return reactiveRepository.findById(id)
                .map(tipoPrestamoMapper::toModel)
                .doOnNext(t -> log.info("TipoPrestamo encontrado: {}", t))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("TipoPrestamo no encontrado con ID: {}", id);
                    return Mono.empty(); // Si no existe, retornamos Mono vacío
                }));
    }

    @Override
    public Flux<TipoPrestamo> findAll() {
        log.debug("Listando todos los Tipos de Prestamo");

        return reactiveRepository.findAll()
                .map(tipoPrestamoMapper::toModel)
                .doOnNext(t -> log.info("TipoPrestamo listado: {}", t.getNombre()));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        log.debug("Verificando existencia de TipoPrestamo con ID: {}", id);
        return reactiveRepository.existsById(id)
                .doOnNext(exists -> log.info("Existe TipoPrestamo con ID {}: {}", id, exists));
    }

    @Override
    public Mono<Boolean> existsByNombreIgnoreCase(String nombre) {
        log.debug("Verificando existencia de TipoPrestamo con nombre (ignore case): {}", nombre);
        return reactiveRepository.existsByNombreIgnoreCase(nombre)
                .doOnNext(exists -> log.info("Existe TipoPrestamo con nombre '{}': {}", nombre, exists));
    }
}
