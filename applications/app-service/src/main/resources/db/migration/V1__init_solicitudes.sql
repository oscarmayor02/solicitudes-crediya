CREATE TABLE IF NOT EXISTS tipo_prestamo (
                                             id_tipo_prestamo BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             nombre VARCHAR(100) NOT NULL UNIQUE,
    monto_minimo DECIMAL(15,2) NOT NULL,
    monto_maximo DECIMAL(15,2) NOT NULL,
    tasa_interes DECIMAL(5,2) NOT NULL, -- % mensual
    validacion_automatica TINYINT(1) NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS estados (
                                       id_estado BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NULL
    );

CREATE TABLE IF NOT EXISTS solicitud (
                                         id_solicitud BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         monto DECIMAL(15,2) NOT NULL,
    plazo INT NOT NULL, -- meses
    email VARCHAR(150) NOT NULL,
    id_usuario BIGINT NOT NULL, -- referencia lógica al micro de autenticación
    id_estado BIGINT NOT NULL,
    id_tipo_prestamo BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitud_estado FOREIGN KEY (id_estado) REFERENCES estados(id_estado),
    CONSTRAINT fk_solicitud_tipo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id_tipo_prestamo),
    INDEX idx_solicitud_usuario (id_usuario),
    INDEX idx_solicitud_email (email)
    );
