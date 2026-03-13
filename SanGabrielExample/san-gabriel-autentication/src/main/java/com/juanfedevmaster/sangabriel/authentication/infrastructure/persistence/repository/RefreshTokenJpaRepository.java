package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revocado = true, r.revokedAt = CURRENT_TIMESTAMP " +
           "WHERE r.usuario.id = :usuarioId AND r.revocado = false")
    void revocarTodosLosTokensActivosPorUsuario(@Param("usuarioId") Long usuarioId);
}
