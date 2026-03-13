package com.juanfedevmaster.sangabriel.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private String tipoDocumento;
    private String telefono;
    private String cargo;
    private String area;
    private boolean estado;
    private boolean cuentaBloqueada;
    private boolean cuentaExpirada;
    private boolean credencialesExpiradas;
    private int intentosFallidos;
    private LocalDateTime ultimoLogin;
    private LocalDateTime ultimoCambioPassword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Rol> roles;
}
