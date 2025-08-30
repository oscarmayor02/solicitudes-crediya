package co.com.pragma.solicitudes.r2dbc.mapper;

import co.com.pragma.solicitudes.model.state.State;
import co.com.pragma.solicitudes.r2dbc.entity.StateEntity;
import org.mapstruct.Mapper;

/**
 * Mapper entre la entidad de base de datos (StateEntity) y el modelo de dominio (State)
 * MapStruct genera automáticamente la implementación.
 */
@Mapper(componentModel = "spring") // Spring inyecta automáticamente el mapper
public interface StateMapper {

    // Convierte de entidad a modelo de dominio
    State toModel(StateEntity entity);

    // Convierte de modelo de dominio a entidad
    StateEntity toEntity(State model);
}
