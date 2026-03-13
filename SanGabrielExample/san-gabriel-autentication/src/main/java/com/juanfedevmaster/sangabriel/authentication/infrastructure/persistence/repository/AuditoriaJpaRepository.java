package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.AuditoriaAutenticacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaAutenticacionEntity, Long> {
}
