package com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.persistence.entity.AuditoriaAutenticacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaAutenticacionJpaRepository extends JpaRepository<AuditoriaAutenticacionEntity, Long> {
}
