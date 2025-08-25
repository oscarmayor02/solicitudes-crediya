package co.com.pragma.solicitudes.r2dbc.mapper;
import co.com.pragma.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre TipoPrestamoEntity y el modelo de dominio TipoPrestamo.
 */
@Mapper(componentModel = "spring")
public interface TipoPrestamoMapper {

    // Convierte la entidad en el modelo de dominio
    TipoPrestamo toModel(TipoPrestamoEntity entity);

    // Convierte el modelo de dominio en la entidad
    TipoPrestamoEntity toEntity(TipoPrestamo model);
}