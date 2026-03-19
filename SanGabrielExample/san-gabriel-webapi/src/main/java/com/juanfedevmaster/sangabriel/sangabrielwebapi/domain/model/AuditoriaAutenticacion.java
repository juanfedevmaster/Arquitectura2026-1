package com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAutenticacion {

    private Long id;
    private String usernameIntentado;
    private EventoAutenticacion evento;
    private String descripcion;
    private LocalDateTime fechaEvento;
    private boolean exitoso;
}
