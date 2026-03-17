package com.juanfedevmaster.sangabriel.authentication.domain.port.out;

import com.juanfedevmaster.sangabriel.authentication.domain.model.AuditoriaAutenticacion;

public interface EventoAutenticacionPublisherPort {
    void publicar(AuditoriaAutenticacion auditoria);
}
