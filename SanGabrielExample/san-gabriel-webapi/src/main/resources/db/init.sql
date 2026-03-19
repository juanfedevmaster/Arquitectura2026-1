-- =============================================
-- Schema: notificaciones
-- =============================================
CREATE SCHEMA IF NOT EXISTS notificaciones;

-- =============================================
-- Tabla: notificaciones.auditoria_autenticacion
-- =============================================
CREATE TABLE IF NOT EXISTS notificaciones.auditoria_autenticacion (
    id               BIGSERIAL     PRIMARY KEY,
    username_intentado VARCHAR(255) NOT NULL,
    evento           VARCHAR(50)   NOT NULL,
    descripcion      TEXT,
    fecha_evento     TIMESTAMP     NOT NULL,
    exitoso          BOOLEAN       NOT NULL
);
