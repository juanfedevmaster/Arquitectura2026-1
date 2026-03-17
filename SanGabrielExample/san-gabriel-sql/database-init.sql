INSERT INTO auth.roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador general del sistema'),
('MEDICO', 'Personal médico'),
('ENFERMERO', 'Personal de enfermería'),
('RECEPCION', 'Personal de recepción'),
('PACIENTE', 'Usuario paciente del portal');

-- Contraseña ejemplo: nunca guardar texto plano
-- Este hash debe venir cifrado desde tu backend (BCrypt recomendado)
INSERT INTO auth.usuarios (
    username,
    email,
    password_hash,
    nombres,
    apellidos,
    documento_identidad,
    tipo_documento,
    telefono,
    cargo,
    area
) VALUES (
    'admin.sangabriel',
    'admin@sangabriel.com',
    '$2y$10$6FSktcvohDNNBHEYLUV.iucNuce8ad4XzQWRvqGO7uDA/7kK1AKv2',
    'Administrador',
    'General',
    '123456789',
    'CC',
    '3000000000',
    'Administrador TI',
    'Tecnologia'
);

INSERT INTO auth.usuarios_roles (usuario_id, rol_id)
VALUES (1, 1);