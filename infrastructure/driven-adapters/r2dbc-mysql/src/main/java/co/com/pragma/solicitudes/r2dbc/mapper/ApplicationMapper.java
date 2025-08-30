package co.com.pragma.solicitudes.r2dbc.mapper;

import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.r2dbc.entity.ApplicationEntity;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre ApplicationEntity (infraestructura) y Application (dominio)
 */
@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    // De entidad a dominio
    Application toModel(ApplicationEntity entity);

    // De dominio a entidad
    ApplicationEntity toEntity(Application model);
}
