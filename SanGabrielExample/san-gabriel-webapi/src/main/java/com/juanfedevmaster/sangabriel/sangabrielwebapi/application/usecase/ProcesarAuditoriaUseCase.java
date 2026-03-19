package com.juanfedevmaster.sangabriel.sangabrielwebapi.application.usecase;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.repository.AuditoriaAutenticacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarAuditoriaUseCase {

    private final AuditoriaAutenticacionRepository repository;

    public void ejecutar(AuditoriaAutenticacion auditoria) {
        log.info("Procesando auditoría: usuario={}, evento={}, exitoso={}",
                auditoria.getUsernameIntentado(),
                auditoria.getEvento(),
                auditoria.isExitoso());
        repository.guardar(auditoria);
    }
}
