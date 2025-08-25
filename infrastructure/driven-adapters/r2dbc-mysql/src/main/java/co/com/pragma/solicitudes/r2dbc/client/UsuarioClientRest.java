package co.com.pragma.solicitudes.r2dbc.client;

import co.com.pragma.solicitudes.model.user.User;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implementación de UsuarioClient que consume el micro de autenticación
 * mediante REST usando WebClient.
 */
@Component
@RequiredArgsConstructor
public class UsuarioClientRest implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UsuarioClientRest.class);

    private final WebClient webClient; // Configurado con baseUrl del micro de autenticación

    @Override
    public Mono<User> getUserById(Long id) {
        log.info("Consultando usuario por ID: {}", id);
        return webClient.get()
                .uri("/api/v1/usuarios/{id}", id)
                .retrieve()
                .bodyToMono(User.class)
                .doOnSuccess(u -> log.info("Usuario encontrado: {}", u))
                .doOnError(e -> log.error("Error consultando usuario: {}", e.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.info("Verificando existencia de email: {}", email);
        return webClient.get()
                .uri("/api/v1/usuarios/exists/email/{email}", email)
                .retrieve()
                .bodyToMono(EmailExistsResponse.class) // map a la clase que contiene "exists"
                .map(EmailExistsResponse::exists)      // extrae el Boolean
                .doOnSuccess(exists -> log.info("Email {} existe: {}", email, exists))
                .doOnError(e -> log.error("Error verificando email: {}", e.getMessage()));
    }
    /**
     * DTO interno para deserializar la respuesta JSON { "exists": true }.
     */
    private static record EmailExistsResponse(Boolean exists) {}
}
