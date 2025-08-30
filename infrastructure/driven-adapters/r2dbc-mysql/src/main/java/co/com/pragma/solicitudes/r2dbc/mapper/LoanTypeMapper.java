package co.com.pragma.solicitudes.r2dbc.mapper;
import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre LoanTypeEntity y el modelo de dominio LoanType.
 */
@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    @Mapping(source = "loanTypeID", target = "loanTypeID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "minimumAmount", target = "minimumAmount")
    @Mapping(source = "maximumAmount", target = "maximumAmount")
    @Mapping(source = "rateInterest", target = "rateInterest")
    @Mapping(source = "automaticValidation", target = "automaticValidation")
    LoanType toModel(LoanTypeEntity entity);

    @Mapping(source = "loanTypeID", target = "loanTypeID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "minimumAmount", target = "minimumAmount")
    @Mapping(source = "maximumAmount", target = "maximumAmount")
    @Mapping(source = "rateInterest", target = "rateInterest")
    @Mapping(source = "automaticValidation", target = "automaticValidation")
    LoanTypeEntity toEntity(LoanType model);
}