package co.com.pragma.solicitudes.r2dbc.mapper;

import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre SolicitudEntity (infraestructura) y Solicitud (dominio)
 */
@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    // De entidad a dominio
    Solicitud toModel(SolicitudEntity entity);

    // De dominio a entidad
    SolicitudEntity toEntity(Solicitud model);
}
