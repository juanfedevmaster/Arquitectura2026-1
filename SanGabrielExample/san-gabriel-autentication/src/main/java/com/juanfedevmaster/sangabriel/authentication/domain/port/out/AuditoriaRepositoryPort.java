package com.juanfedevmaster.sangabriel.authentication.domain.port.out;

import com.juanfedevmaster.sangabriel.authentication.domain.model.AuditoriaAutenticacion;

public interface AuditoriaRepositoryPort {

    AuditoriaAutenticacion registrar(AuditoriaAutenticacion auditoria);
}
