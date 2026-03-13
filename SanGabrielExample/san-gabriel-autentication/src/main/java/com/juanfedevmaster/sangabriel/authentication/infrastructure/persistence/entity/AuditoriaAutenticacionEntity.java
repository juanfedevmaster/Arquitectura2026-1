package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity;

import com.juanfedevmaster.sangabriel.authentication.domain.model.EventoAutenticacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_autenticacion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAutenticacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "username_intentado", length = 150)
    private String usernameIntentado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventoAutenticacion evento;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(nullable = false)
    private boolean exitoso;
}
