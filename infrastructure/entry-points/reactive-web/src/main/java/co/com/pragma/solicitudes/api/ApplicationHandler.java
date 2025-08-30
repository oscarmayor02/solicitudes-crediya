package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.api.mapper.ApplicationMapper;
import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.enums.CodeState;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.application.ApplicationUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final ApplicationUseCase useCase;
    private final UserRepository userRepository;
    private final LoanTypeRepository loanTypeRepository;

    private static final Logger log = LoggerFactory.getLogger(ApplicationHandler.class);

    // 🔑 Se inyecta desde application.yml
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Crear nueva solicitud
     * POST /api/v1/solicitudes
     */
    public Mono<ServerResponse> createApplication(ServerRequest request) {
        log.info(ApplicationConstants.LOG_APPLICATION_RECIBIDA_CREAR);

        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(401)
                    .bodyValue(ApplicationConstants.MSG_NO_TOKEN);
        }
        String token = authHeader.substring(7);

        return request.bodyToMono(Application.class)
                .doOnNext(s -> log.debug(ApplicationConstants.LOG_CUERPO_RECIBIDO, s))
                .flatMap(solicitud -> useCase.execute(solicitud, token))
                .flatMap(s -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s));
    }

    /**
     * Listar todas las solicitudes
     * GET /api/v1/solicitudes
     */
    public Mono<ServerResponse> listApplications(ServerRequest request) {
        log.info(ApplicationConstants.LOG_LISTAR_SOLICITUDES);

        Flux<Application> solicitudes = useCase.getAllApplication();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitudes, Application.class);
    }

    /**
     * Listar solicitudes que requieren revisión manual.
     * GET /api/v1/solicitudes/revision?estado=RECHAZADA
     * Si no se envía query param → muestra PENDIENTE_REVISION.
     * Solo accesible con rol ASESOR o ADMIN.
     */
    public Mono<ServerResponse> listReviewApplication(ServerRequest request) {
        String estadoParam = request.queryParam("estado").orElse(CodeState.PENDIENTE_REVISION.name());
        CodeState estado;
        try {
            estado = CodeState.valueOf(estadoParam);
        } catch (IllegalArgumentException e) {
            log.warn(ApplicationConstants.LOG_ESTADO_INVALIDO, estadoParam);
            estado = CodeState.PENDIENTE_REVISION;
        }

        log.info(ApplicationConstants.LOG_LISTING_APPLICATIONS_ESTADO, estado);

        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn(ApplicationConstants.MSG_NO_TOKEN);
            return ServerResponse.status(401).bodyValue(ApplicationConstants.MSG_NO_TOKEN);
        }

        String rawToken = authHeader.substring(7).trim();

        Claims claims;
        try {
            claims = getClaimsFromToken(rawToken, jwtSecret);
        } catch (Exception e) {
            log.warn(ApplicationConstants.MSG_TOKEN_INVALIDO + ": {}", e.getMessage());
            return ServerResponse.status(401).bodyValue(ApplicationConstants.MSG_TOKEN_INVALIDO);
        }

        List<String> roles = claims.get("roles", List.class);
        if (roles == null || (!roles.contains("ASESOR") && !roles.contains("ADMIN"))) {
            log.warn("Usuario sin permisos: roles={}", roles);
            return ServerResponse.status(403).bodyValue(ApplicationConstants.MSG_SIN_PERMISOS);
        }

        return useCase.getApplicationsByState(estado.getId())
                .flatMap(app -> Mono.zip(
                        Mono.just(app),
                        userRepository.getUserById(app.getIdUser(), rawToken),
                        loanTypeRepository.findById(app.getLoanTypeID())
                ))
                .map(tuple -> ApplicationMapper.toResponse(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list))
                .doOnError(e -> log.error(ApplicationConstants.LOG_ERROR_LISTANDO_APLICACIONES, e.getMessage()));
    }

    /**
     * Obtener solicitud por ID
     * GET /api/v1/solicitudes/{id}
     */
    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info(ApplicationConstants.LOG_APPLICATION_OBTENER_ID, id);

        return useCase.getApplicationById(id)
                .flatMap(s -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Editar solicitud
     * PUT /api/v1/solicitudes
     */
    public Mono<ServerResponse> editApplication(ServerRequest request) {
        log.info(ApplicationConstants.LOG_APPLICATION_EDITAR);
        return request.bodyToMono(Application.class)
                .flatMap(useCase::editApplication)
                .flatMap(s -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s));
    }

    /**
     * Eliminar solicitud por ID
     * DELETE /api/v1/solicitudes/{id}
     */
    public Mono<ServerResponse> deleteApplication(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info(ApplicationConstants.LOG_APPLICATION_ELIMINAR, id);
        return useCase.delete(id)
                .then(ServerResponse.noContent().build());
    }

    /**
     * Decodifica el JWT para obtener claims
     */
    private Claims getClaimsFromToken(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
