CREATE TABLE IF NOT EXISTS loan_type (
                                        loan_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             name VARCHAR(100) NOT NULL UNIQUE,
    minimumAmount DECIMAL(15,2) NOT NULL,
    maximumAmount DECIMAL(15,2) NOT NULL,
    rateInterest DECIMAL(5,2) NOT NULL, -- % mensual
    automaticValidation TINYINT(1) NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS state (
                                     id_state BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200) NULL
    );

CREATE TABLE IF NOT EXISTS application (
                                           id_application BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           amount DECIMAL(15,2) NOT NULL,
    term INT NOT NULL, -- meses
    email VARCHAR(150) NOT NULL,
    id_user BIGINT NOT NULL, -- referencia lógica al micro de autenticación
    id_state BIGINT NOT NULL,
    loan_type_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitud_estado FOREIGN KEY (id_state) REFERENCES state(id_state),
    CONSTRAINT fk_solicitud_tipo FOREIGN KEY (loan_type_id) REFERENCES loan_type(loan_type_id),
    INDEX idx_solicitud_usuario (loan_type_id),
    INDEX idx_solicitud_email (email)
    );
