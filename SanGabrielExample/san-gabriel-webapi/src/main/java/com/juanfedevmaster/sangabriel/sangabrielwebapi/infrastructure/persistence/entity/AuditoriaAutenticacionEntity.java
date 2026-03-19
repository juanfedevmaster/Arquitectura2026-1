package com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.persistence.entity;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.EventoAutenticacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_autenticacion", schema = "notificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAutenticacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username_intentado", nullable = false)
    private String usernameIntentado;

    @Enumerated(EnumType.STRING)
    @Column(name = "evento", nullable = false)
    private EventoAutenticacion evento;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "exitoso", nullable = false)
    private boolean exitoso;
}
