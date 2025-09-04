package co.com.pragma.solicitudes.model.constants;

import lombok.experimental.UtilityClass;

/**
 * Clase que centraliza todos los mensajes y constantes reutilizables
 * en la capa de ApplicationUseCase.
 */
@UtilityClass
public final class ApplicationConstants {


    // Mensajes de validación
    public static final String MSG_MANDATORY_FIELDS =
            "Monto, plazo y email son obligatorios";
    public static final String MSG_LOAN_TYPE_REQUIRED =
            "Debe indicar el tipo de préstamo";
    public static final String MSG_EMAIL_NOT_FOUND =
            "El email proporcionado no existe en el sistema";
    public static final String MSG_LOAN_TYPE_NOT_FOUND =
            "Tipo de préstamo no encontrado";
    public static final String MSG_AMOUNT_OUT_OF_RANGE =
            "Monto fuera del rango permitido para este tipo de préstamo";
    public static final String MSG_APPLICATION_NOT_FOUND =
            "Application no encontrada";
    public static final String MSG_APPLICATION_NOT_FOUND_EDIT =
            "Application no encontrada para editar";

    // Logs
    public static final String LOG_START_CREATION =
            "Iniciando creación de Application para usuario: {}";
    public static final String LOG_USER_FOUND =
            "Usuario encontrado: {}";
    public static final String LOG_EMAIL_EXISTS =
            "Email existe en sistema: {}";
    public static final String LOG_APP_CREATED =
            "Application creada con ID: {}";
    public static final String LOG_CREATION_ERROR =
            "Error al crear Application: {}";

    // ===================== CÓDIGOS =====================
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String TIPO_PRESTAMO_NO_EXISTE = "TIPO_PRESTAMO_NO_EXISTE";
    public static final String MONTO_FUERA_DE_RANGO = "MONTO_FUERA_DE_RANGO";
    public static final String PLAZO_INVALIDO = "PLAZO_INVALIDO";
    public static final String DATOS_OBLIGATORIOS = "DATOS_OBLIGATORIOS";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    // ===================== MENSAJES =====================
    public static final String MSG_NOT_FOUND = "El recurso solicitado no fue encontrado.";
    public static final String MSG_TIPO_PRESTAMO_NO_EXISTE = "El tipo de préstamo especificado no existe.";
    public static final String MSG_MONTO_FUERA_DE_RANGO = "El monto solicitado está fuera del rango permitido.";
    public static final String MSG_PLAZO_INVALIDO = "El plazo especificado no es válido.";
    public static final String MSG_DATOS_OBLIGATORIOS = "Faltan datos obligatorios para procesar la solicitud.";
    public static final String MSG_VALIDATION_ERROR = "Se encontraron errores de validación en los datos proporcionados.";


    // ===================== LOAN TYPE =====================

    public static final String NAME_ALREADY_EXISTS = "Ya existe un type de préstamo con ese nombre";
    public static final String LOAN_TYPE_NOT_FOUND = "Tipo de préstamo no encontrado";
    public static final String NAME_REQUIRED = "El nombre es obligatorio";
    public static final String AMOUNTS_REQUIRED = "Montos mínimo y máximo son obligatorios";
    public static final String MIN_AMOUNT_NEGATIVE = "El monto mínimo no puede ser negativo";
    public static final String MAX_AMOUNT_INVALID = "El monto máximo debe ser >= monto mínimo";
    public static final String INTEREST_REQUIRED = "La tasa de interés debe ser > 0";
    public static final String LOG_SAVE_LOANTYPE = "Guardando LoanType: {}";
    public static final String LOG_LOANTYPE_SAVE = "LoanType guardado con ID: {}";

    public static final String LOG_FOUND_BY_ID = "Buscando LoanType con ID: {}";
    public static final String LOG_LOANTYPE_FOUND = "LoanType encontrado: {}";
    public static final String LOG_LOANTYPE_NO_FOUND = "LoanType no encontrado con ID: {}";

    public static final String LOG_LIST_LOAN = "Listando todos los Tipos de Prestamo";
    public static final String LOG_LOANTYPE_LIST = "LoanType listado: {}";

    public static final String LOG_VERIFICate_EXISTEN_BY_ID = "Verificando existencia de LoanType con ID: {}";
    public static final String LOG_EXIST_NY_ID = "Existe LoanType con ID {}: {}";

    public static final String LOG_VERIFICATE_EXISTEN_BY_NAME = "Verificando existencia de LoanType con nombre (ignore case): {}";
    public static final String LOG_EXIST_BY_NAME = "Existe LoanType con nombre '{}': {}";

    public static final String ERROR_NAME_DESCRIPTION_REQUIRED =
            "Nombre y descripción son obligatorios";

    // ===================== USER CLIENT REST =====================

    public static final String LOG_GET_USER = "Consultando usuario por ID: {} con token";
    public static final String LOG_USER_FOUND_OK = "Usuario encontrado: {}";
    public static final String LOG_ERROR_CONSULTA_USER = "Error consultando usuario: {}";

    public static final String LOG_VERIFICATE_EMAIL = "Verificando existencia de email: {} con token";
    public static final String LOG_EMAIL_EXIST = "Email {} existe: {}";
    public static final String LOG_ERROR_VERIFICATE_EMAIL = "Error verificando email: {}";

    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final String URI_GET_USER_BY_ID = "/api/v1/usuarios/{id}";
    public static final String URI_EXISTS_EMAIL = "/api/v1/usuarios/exists/email/{email}";

    // ===================== APPLICATION=====================
    public static final String LOG_SAVE_APPLICATION = "Guardando Application: {}";
    public static final String LOG_APPLICATION_SAVE = "Application guardada con ID: {}";

    public static final String LOG_LIST_ALL = "Listando todas las Solicitudes";
    public static final String LOG_APPLICATION_LIST = "Application listada: {}";


    // Rutas
    public static final String RUTA_SOLICITUD = "/api/v1/solicitudes";
    public static final String RUTA_SOLICITUD_REVISION = "/api/v1/solicitudes/revision";

    // Logs generales
    public static final String LOG_APPLICATION_RECIBIDA_CREAR = "Application recibida para crear nueva Application";
    public static final String LOG_CUERPO_RECIBIDO = "Cuerpo recibido: {}";
    public static final String LOG_LISTAR_SOLICITUDES = "Application para listar todas las solicitudes";
    public static final String LOG_LISTING_APPLICATIONS_ESTADO = "Listing applications for estado: {}";
    public static final String LOG_ESTADO_INVALIDO = "Estado inválido recibido: {}. Se usará PENDIENTE_REVISION";
    public static final String LOG_APPLICATION_OBTENER_ID = "Application para obtener solicitud con ID: {}";
    public static final String LOG_APPLICATION_EDITAR = "Application para editar solicitud";
    public static final String LOG_APPLICATION_ELIMINAR = "Application para eliminar solicitud con ID: {}";
    public static final String LOG_ERROR_LISTANDO_APLICACIONES = "Error listando aplicaciones en revisión: {}";

    // Mensajes de error HTTP / validaciones
    public static final String MSG_NO_TOKEN = "No se proporcionó token de autenticación";
    public static final String MSG_TOKEN_INVALIDO = "Token inválido";
    public static final String MSG_SIN_PERMISOS = "No tienes permiso para acceder a este recurso";
// ===================== NUEVAS CONSTANTES =====================

    // ===== Ruta singular para decisiones =====
    public static final String RUTA_SOLICITUD_DECISION = "/api/v1/solicitud"; // Nuevo endpoint PUT para aprobar/rechazar

    // ===== Mensajes para flujo de decisión =====
    public static final String MSG_DECISION_ALLOWED = "Solo se permiten decisiones APROBADA o RECHAZADA";
    public static final String MSG_DECISION_FORBIDDEN = "No tiene permisos para tomar decisiones";
    public static final String MSG_DECISION_PROCESS_ERROR = "Ocurrió un error procesando la decisión";

    // ===== Logs específicos de flujo de decisión =====
    public static final String LOG_DECISION_RECEIVED = "[HANDLER] Petición de decisión recibida";
    public static final String LOG_DECISION_APPLY = "[USECASE] Decisión aplicada idApp={} nuevoEstado={} corr={}";
    public static final String LOG_DECISION_ERROR = "[USECASE] Error al aplicar decisión idApp={} corr={} err={}";

    // ===== Logs para integración con SQS =====
    public static final String LOG_SQS_PUBLISHING = "[SQS] Publicando decisión idApp={} decision={} corr={}";
    public static final String LOG_SQS_PUBLISHED = "[SQS] Decisión publicada idApp={} messageId={}";
    public static final String LOG_SQS_PUBLISH_ERROR = "[SQS] Error publicando decisión idApp={} corr={} err={}";

    // ===== Headers HTTP para trazabilidad =====
    public static final String HDR_AUTHORIZATION = "Authorization";       // Header estándar para token Bearer
    public static final String HDR_CORRELATION_ID = "X-Correlation-Id";   // Header para trazabilidad en logs

    // ===== Log para token ausente =====
    public static final String LOG_MISSING_BEARER = "No se recibió token o formato inválido";



















































































    public static final String LOG_APPLICATION_ENCONTRADA = "Application encontrada: {}";
    public static final String LOG_APPLICATION_NO_ENCONTRADA = "Application no encontrada con ID: {}";

    public static final String LOG_ELIMINANDO_POR_ID = "Eliminando Application con ID: {}";
    public static final String LOG_APPLICATION_ELIMINADA = "Application eliminada con ID: {}";

    public static final String LOG_LISTANDO_POR_ESTADOS = "Listando solicitudes con estados: {}";
    public static final String LOG_APPLICATION_PARA_REVISION = "Application para revisión: {}";
}

