package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 100)
    private String nombres;

    @Column(length = 100)
    private String apellidos;

    @Column(name = "documento_identidad", length = 50)
    private String documentoIdentidad;

    @Column(name = "tipo_documento", length = 30)
    private String tipoDocumento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String cargo;

    @Column(length = 100)
    private String area;

    @Column(nullable = false)
    private boolean estado;

    @Column(name = "cuenta_bloqueada", nullable = false)
    private boolean cuentaBloqueada;

    @Column(name = "cuenta_expirada", nullable = false)
    private boolean cuentaExpirada;

    @Column(name = "credenciales_expiradas", nullable = false)
    private boolean credencialesExpiradas;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "ultimo_cambio_password")
    private LocalDateTime ultimoCambioPassword;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<RolEntity> roles;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
