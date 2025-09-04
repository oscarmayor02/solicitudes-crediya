    package co.com.pragma.solicitudes.api.mapper;

    import co.com.pragma.solicitudes.api.dto.ApplicationResponse;
    import co.com.pragma.solicitudes.model.application.Application;
    import co.com.pragma.solicitudes.model.enums.CodeState;
    import co.com.pragma.solicitudes.model.loantype.LoanType;
    import co.com.pragma.solicitudes.model.user.User;

    public class ApplicationMapper {

        public static ApplicationResponse toResponse(Application app, User user, LoanType loan) {
            return ApplicationResponse.builder()
                    .idApplication(app.getIdApplication())
                    .amount(app.getAmount())
                    .term(app.getTerm())
                    .email(app.getEmail())
                    .name(user.getName() + " " + user.getLastName())
                    .loanType(loan.getName())
                    .rateInterest(loan.getRateInterest())
                    .stateApplication(CodeState.fromId(app.getIdState()).name())
                    .baseSalary(user.getBaseSalary())
                    .build();
        }
    }
