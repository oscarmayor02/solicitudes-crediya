package co.com.pragma.solicitudes.r2dbc.mapper;
import co.com.pragma.solicitudes.model.solicitud.Solicitud;
import co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre entidad y modelo de dominio.
 */
@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    Solicitud toModel(co.com.pragma.solicitudes.r2dbc.entity.SolicitudEntity entity);       // De entidad a dominio
    SolicitudEntity toEntity(Solicitud model);       // De dominio a entidad
}
