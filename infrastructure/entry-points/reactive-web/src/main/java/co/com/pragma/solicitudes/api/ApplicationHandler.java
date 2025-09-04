package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.api.dto.DecisionRequest;
import co.com.pragma.solicitudes.api.mapper.ApplicationMapper;
import co.com.pragma.solicitudes.model.application.Application;
import co.com.pragma.solicitudes.model.constants.ApplicationConstants;
import co.com.pragma.solicitudes.model.enums.CodeState;
import co.com.pragma.solicitudes.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.solicitudes.model.state.gateways.StateRepository;
import co.com.pragma.solicitudes.model.user.gateways.UserRepository;
import co.com.pragma.solicitudes.usecase.application.ApplicationUseCase;
import co.com.pragma.solicitudes.usecase.exceptions.DomainExceptions;
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
    private final StateRepository stateRepository;
    private static final Logger log = LoggerFactory.getLogger(ApplicationHandler.class);

    // Se inyecta desde application.yml
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
                .flatMap(saved ->
                        // Zipear consultas: usuario y loan type (en paralelo)
                        Mono.zip(
                                userRepository.getUserById(saved.getIdUser(), token),
                                loanTypeRepository.findById(saved.getLoanTypeID())
                        ).map(tuple -> {
                            var user = tuple.getT1();
                            var loan = tuple.getT2();
                            // Mapear a DTO enriquecido
                            return ApplicationMapper.toResponse(saved, user, loan);
                        })
                )
                // 3) Responder con DTO enriquecido
                .flatMap(respDto -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(respDto)
                );
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
    /**
     * PUT /api/v1/solicitud
     * Requiere rol ASESOR.
     * Body: DecisionRequest { idApplication, decision, observations }
     */
    public Mono<ServerResponse> decide(ServerRequest request) {
        log.info(ApplicationConstants.LOG_DECISION_RECEIVED);

        // 1) Valida header Authorization: Bearer <token>
        String authHeader = request.headers().firstHeader(ApplicationConstants.HDR_AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
            log.warn(ApplicationConstants.LOG_MISSING_BEARER);
            return ServerResponse.status(401).contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(ApplicationConstants.MSG_NO_TOKEN);
        }
        String rawToken = authHeader.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length()).trim();

        // 2) Parse claims del JWT (tolerante a distintos formatos de roles)
        final Claims claims;
        try {
            claims = getClaimsFromToken(rawToken, jwtSecret);
        } catch (Exception e) {
            log.warn("{}: {}", ApplicationConstants.MSG_TOKEN_INVALIDO, e.getMessage());
            return ServerResponse.status(401).contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(ApplicationConstants.MSG_TOKEN_INVALIDO);
        }

        // 3) Verifica rol ASESOR (acepta String, Lista<String>, y Lista<Map> tipo {"authority": "..."/"role": "..."})
        if (!hasAsesorRole(claims.get("roles"))) {
            return ServerResponse.status(403).contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(ApplicationConstants.MSG_DECISION_FORBIDDEN);
        }

        // 4) Toma o genera Correlation-Id
        String correlationId = request.headers().firstHeader(ApplicationConstants.HDR_CORRELATION_ID);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = java.util.UUID.randomUUID().toString();
        }

        // 5) Lee body y ejecuta caso de uso
        final String corrId = correlationId;

        // Nota: forzamos application/json para parseo correcto
        return request.bodyToMono(DecisionRequest.class)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Body vacío corr={}", corrId);
                    return Mono.error(new DomainExceptions.ValidationException("Body vacío o inválido"));
                }))
                .flatMap(dto -> {
                    // Validaciones mínimas de DTO
                    if (dto.getIdApplication() == null) {
                        return Mono.error(new DomainExceptions.ValidationException("idApplication es obligatorio"));
                    }
                    if (dto.getDecision() == null || dto.getDecision().isBlank()) {
                        return Mono.error(new DomainExceptions.ValidationException(ApplicationConstants.MSG_DECISION_ALLOWED));
                    }

                    final CodeState state;
                    try {
                        // Acepta “aprobada”/“rechazada” en cualquier case
                        state = CodeState.valueOf(dto.getDecision().trim().toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        return Mono.error(new DomainExceptions.ValidationException(ApplicationConstants.MSG_DECISION_ALLOWED));
                    }

                    return useCase.decide(dto.getIdApplication(), state, rawToken, corrId, dto.getObservations());
                })
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(h -> h.add(ApplicationConstants.HDR_CORRELATION_ID, corrId)) // devolvemos el correlation-id
                        .bodyValue(saved))
                .onErrorResume(DomainExceptions.NotFound.class,
                        e -> ServerResponse.status(404)
                                .contentType(MediaType.TEXT_PLAIN)
                                .headers(h -> h.add(ApplicationConstants.HDR_CORRELATION_ID, corrId))
                                .bodyValue(e.getMessage()))
                .onErrorResume(DomainExceptions.ValidationException.class,
                        e -> ServerResponse.badRequest()
                                .contentType(MediaType.TEXT_PLAIN)
                                .headers(h -> h.add(ApplicationConstants.HDR_CORRELATION_ID, corrId))
                                .bodyValue(e.getMessage()))
                // Si el publisher SQS explota, lo tratamos como 502 (upstream de mensajería)
                .onErrorResume(software.amazon.awssdk.core.exception.SdkException.class,
                        e -> {
                            log.error("Fallo SQS corr={} err={}", corrId, e.toString());
                            return ServerResponse.status(502)
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .headers(h -> h.add(ApplicationConstants.HDR_CORRELATION_ID, corrId))
                                    .bodyValue("Error publicando la decisión. Intenta nuevamente.");
                        })
                .onErrorResume(e -> {
                    log.error("{} corr={} err={}", ApplicationConstants.MSG_DECISION_PROCESS_ERROR, corrId, e.toString());
                    return ServerResponse.status(500)
                            .contentType(MediaType.TEXT_PLAIN)
                            .headers(h -> h.add(ApplicationConstants.HDR_CORRELATION_ID, corrId))
                            .bodyValue(ApplicationConstants.MSG_DECISION_PROCESS_ERROR);
                });
    }

    /**
     * Extrae el rol ASESOR desde distintos formatos posibles dentro del claim "roles".
     */
    @SuppressWarnings("unchecked")
    private boolean hasAsesorRole(Object rolesClaim) {
        if (rolesClaim == null) return false;

        // 1) String simple: "ASESOR,OTRO" o "ASESOR"
        if (rolesClaim instanceof String s) {
            for (String part : s.split(",")) {
                if ("ASESOR".equalsIgnoreCase(part.trim())) return true;
            }
            return false;
        }

        // 2) Lista de Strings
        if (rolesClaim instanceof java.util.Collection<?> col) {
            for (Object o : col) {
                if (o instanceof String s && "ASESOR".equalsIgnoreCase(s)) return true;
                if (rolesClaim instanceof java.util.Map<?, ?> m) {
                    Object v = m.get("authority");
                    if (v == null) {
                        v = m.get("role");
                    }
                    return (v instanceof String sv) && "ASESOR".equalsIgnoreCase(sv);
                }
            }
            return false;
        }

        // 3) Mapa único { "authority": "ASESOR" } o { "role": "ASESOR" }
        if (rolesClaim instanceof java.util.Map<?, ?> m) {
            Object v = m.get("authority");
            if (v == null) {
                v = m.get("role");
            }
            return (v instanceof String sv) && "ASESOR".equalsIgnoreCase(sv);
        }

        return false;
    }

}
