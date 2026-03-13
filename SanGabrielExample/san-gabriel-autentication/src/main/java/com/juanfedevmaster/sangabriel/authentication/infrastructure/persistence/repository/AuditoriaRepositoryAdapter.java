package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.authentication.domain.model.AuditoriaAutenticacion;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.AuditoriaRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.AuditoriaAutenticacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditoriaRepositoryAdapter implements AuditoriaRepositoryPort {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    @Override
    public AuditoriaAutenticacion registrar(AuditoriaAutenticacion auditoria) {
        AuditoriaAutenticacionEntity entity = AuditoriaAutenticacionEntity.builder()
                .usuarioId(auditoria.getUsuarioId())
                .usernameIntentado(auditoria.getUsernameIntentado())
                .evento(auditoria.getEvento())
                .descripcion(auditoria.getDescripcion())
                .ipOrigen(auditoria.getIpOrigen())
                .userAgent(auditoria.getUserAgent())
                .fechaEvento(auditoria.getFechaEvento())
                .exitoso(auditoria.isExitoso())
                .build();

        AuditoriaAutenticacionEntity saved = auditoriaJpaRepository.save(entity);

        return AuditoriaAutenticacion.builder()
                .id(saved.getId())
                .usuarioId(saved.getUsuarioId())
                .usernameIntentado(saved.getUsernameIntentado())
                .evento(saved.getEvento())
                .descripcion(saved.getDescripcion())
                .ipOrigen(saved.getIpOrigen())
                .userAgent(saved.getUserAgent())
                .fechaEvento(saved.getFechaEvento())
                .exitoso(saved.isExitoso())
                .build();
    }
}
