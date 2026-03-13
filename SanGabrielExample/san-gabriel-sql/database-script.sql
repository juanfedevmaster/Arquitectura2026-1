-- =========================================================
-- ESQUEMA BASE
-- =========================================================
CREATE SCHEMA IF NOT EXISTS auth;

-- =========================================================
-- TABLA: roles
-- =========================================================
CREATE TABLE auth.roles (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL UNIQUE,
    descripcion     VARCHAR(255),
    estado          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- TABLA: usuarios
-- =========================================================
CREATE TABLE auth.usuarios (
    id                      BIGSERIAL PRIMARY KEY,
    username                VARCHAR(50) NOT NULL UNIQUE,
    email                   VARCHAR(120) NOT NULL UNIQUE,
    password_hash           VARCHAR(255) NOT NULL,
    nombres                 VARCHAR(100) NOT NULL,
    apellidos               VARCHAR(100) NOT NULL,
    documento_identidad     VARCHAR(30) UNIQUE,
    tipo_documento          VARCHAR(20),
    telefono                VARCHAR(20),
    cargo                   VARCHAR(80),
    area                    VARCHAR(80),
    estado                  BOOLEAN NOT NULL DEFAULT TRUE,
    cuenta_bloqueada        BOOLEAN NOT NULL DEFAULT FALSE,
    cuenta_expirada         BOOLEAN NOT NULL DEFAULT FALSE,
    credenciales_expiradas  BOOLEAN NOT NULL DEFAULT FALSE,
    intentos_fallidos       INT NOT NULL DEFAULT 0,
    ultimo_login            TIMESTAMP,
    ultimo_cambio_password  TIMESTAMP,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- TABLA INTERMEDIA: usuarios_roles
-- =========================================================
CREATE TABLE auth.usuarios_roles (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL,
    rol_id          BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_roles_usuario
        FOREIGN KEY (usuario_id) REFERENCES auth.usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_roles_rol
        FOREIGN KEY (rol_id) REFERENCES auth.roles(id)
        ON DELETE RESTRICT,
    CONSTRAINT uq_usuario_rol UNIQUE (usuario_id, rol_id)
);

-- =========================================================
-- TABLA: refresh_tokens
-- =========================================================
CREATE TABLE auth.refresh_tokens (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL,
    token               VARCHAR(500) NOT NULL UNIQUE,
    fecha_expiracion    TIMESTAMP NOT NULL,
    revocado            BOOLEAN NOT NULL DEFAULT FALSE,
    ip_origen           VARCHAR(50),
    user_agent          VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at          TIMESTAMP,
    CONSTRAINT fk_refresh_token_usuario
        FOREIGN KEY (usuario_id) REFERENCES auth.usuarios(id)
        ON DELETE CASCADE
);

-- =========================================================
-- TABLA: auditoria_autenticacion
-- =========================================================
CREATE TABLE auth.auditoria_autenticacion (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT,
    username_intentado  VARCHAR(50),
    evento              VARCHAR(50) NOT NULL,
    descripcion         VARCHAR(255),
    ip_origen           VARCHAR(50),
    user_agent          VARCHAR(255),
    fecha_evento        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exitoso             BOOLEAN NOT NULL,
    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (usuario_id) REFERENCES auth.usuarios(id)
        ON DELETE SET NULL
);

-- =========================================================
-- ÍNDICES
-- =========================================================
CREATE INDEX idx_usuarios_email ON auth.usuarios(email);
CREATE INDEX idx_usuarios_username ON auth.usuarios(username);
CREATE INDEX idx_refresh_tokens_usuario_id ON auth.refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_tokens_token ON auth.refresh_tokens(token);
CREATE INDEX idx_auditoria_usuario_id ON auth.auditoria_autenticacion(usuario_id);
CREATE INDEX idx_auditoria_fecha_evento ON auth.auditoria_autenticacion(fecha_evento);