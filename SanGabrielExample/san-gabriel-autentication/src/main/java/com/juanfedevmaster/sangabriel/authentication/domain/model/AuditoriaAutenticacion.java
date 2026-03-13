package com.juanfedevmaster.sangabriel.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAutenticacion {

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
