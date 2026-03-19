package com.juanfedevmaster.sangabriel.sangabrielwebapi.application.dto;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.EventoAutenticacion;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa el mensaje completo recibido desde Azure Service Bus.
 * Actúa como Adaptee en el patrón Adapter.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAutenticacionMensaje {

    private Long id;
    private Long usuarioId;
    private String usernameIntentado;
    private EventoAutenticacion evento;
    private String descripcion;
    private String ipOrigen;
    private String userAgent;
    private LocalDateTime fechaEvento;
    private boolean exitoso;
}
