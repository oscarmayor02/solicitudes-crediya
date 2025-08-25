package co.com.pragma.solicitudes.r2dbc.mapper;

import co.com.pragma.solicitudes.model.estado.Estado;
import co.com.pragma.solicitudes.r2dbc.entity.EstadoEntity;
import org.mapstruct.Mapper;

/**
 * Mapper entre la entidad de base de datos (EstadoEntity) y el modelo de dominio (Estado)
 * MapStruct genera automáticamente la implementación.
 */
@Mapper(componentModel = "spring") // Spring inyecta automáticamente el mapper
public interface EstadoMapper {

    // Convierte de entidad a modelo de dominio
    Estado toModel(EstadoEntity entity);

    // Convierte de modelo de dominio a entidad
    EstadoEntity toEntity(Estado model);
}
