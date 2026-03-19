package com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.repository.AuditoriaAutenticacionRepository;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.persistence.entity.AuditoriaAutenticacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de salida (output adapter) en la arquitectura hexagonal.
 * Implementa el puerto de dominio AuditoriaAutenticacionRepository
 * usando Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class AuditoriaAutenticacionRepositoryImpl implements AuditoriaAutenticacionRepository {

    private final AuditoriaAutenticacionJpaRepository jpaRepository;

    @Override
    public void guardar(AuditoriaAutenticacion auditoria) {
        AuditoriaAutenticacionEntity entity = AuditoriaAutenticacionEntity.builder()
                .id(auditoria.getId())
                .usernameIntentado(auditoria.getUsernameIntentado())
                .evento(auditoria.getEvento())
                .descripcion(auditoria.getDescripcion())
                .fechaEvento(auditoria.getFechaEvento())
                .exitoso(auditoria.isExitoso())
                .build();
        jpaRepository.save(entity);
    }
}
