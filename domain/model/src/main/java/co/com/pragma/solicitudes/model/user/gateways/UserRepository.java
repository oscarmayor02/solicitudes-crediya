package co.com.pragma.solicitudes.model.user.gateways;

import co.com.pragma.solicitudes.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Interfaz que define las operaciones que necesita el micro de solicitudes
 * para consultar usuarios del micro de autenticación.
 *
 * Esta interfaz es parte del dominio de solicitudes y no depende
 * de WebClient, Spring o cualquier infraestructura.
 */
public interface UserRepository {

    /**
     * Consulta un usuario por su ID en el micro de autenticación.
     *
     * @param id del usuario
     * @param token JWT de autenticación
     * @return Mono<User> si existe
     */
    Mono<User> getUserById(Long id, String token);

    /**
     * Verifica si un email existe en el micro de autenticación.
     *
     * @param email email a consultar
     * @param token JWT de autenticación
     * @return Mono<Boolean> true si existe, false si no
     */
    Mono<Boolean> existsByEmail(String email, String token);
}
