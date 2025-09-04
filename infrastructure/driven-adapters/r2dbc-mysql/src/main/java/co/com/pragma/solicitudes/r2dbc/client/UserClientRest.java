package co.com.pragma.solicitudes.r2dbc.client;

import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.user.User;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserClientRest implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserClientRest.class);
    private final WebClient webClient; // definido en WebClientsConfig

    @Override
    public Mono<User> getUserById(Long id, String token) {
        log.info(ApplicationConstants.LOG_GET_USER, id);
        return webClient.get()
                .uri(ApplicationConstants.URI_GET_USER_BY_ID, id)
                .headers(h -> h.setBearerAuth(token))   // 👈 evita problemas de prefijo y espacios
                .retrieve()
                .bodyToMono(User.class)
                .doOnSuccess(u -> log.info(ApplicationConstants.LOG_USER_FOUND_OK, u))
                .doOnError(e -> log.error(ApplicationConstants.LOG_ERROR_CONSULTA_USER, e.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email, String token) {
        log.info(ApplicationConstants.LOG_VERIFICATE_EMAIL, email);
        return webClient.get()
                .uri(ApplicationConstants.URI_EXISTS_EMAIL, email)
                .headers(h -> h.setBearerAuth(token))   // 👈
                .retrieve()
                .bodyToMono(EmailExistsResponse.class)
                .map(EmailExistsResponse::exists)
                .doOnSuccess(exists -> log.info(ApplicationConstants.LOG_EMAIL_EXIST, email, exists))
                .doOnError(e -> log.error(ApplicationConstants.LOG_ERROR_VERIFICATE_EMAIL, e.getMessage()));
    }

    private static record EmailExistsResponse(Boolean exists) {}
}
