package co.com.pragma.solicitudes.r2dbc.mapper;
import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.r2dbc.entity.EstadoEntity;
import org.mapstruct.Mapper;

/**
 * Mapper entre Estado y EstadoEntity
 */
@Mapper(componentModel = "spring")
public interface EstadoMapper {
    Estado toModel(EstadoEntity entity);
    EstadoEntity toEntity(Estado model);
}