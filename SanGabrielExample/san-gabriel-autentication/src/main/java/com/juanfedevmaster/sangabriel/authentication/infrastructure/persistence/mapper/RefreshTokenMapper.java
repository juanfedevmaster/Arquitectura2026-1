package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.mapper;

import com.juanfedevmaster.sangabriel.authentication.domain.model.RefreshToken;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return RefreshToken.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .token(entity.getToken())
                .fechaExpiracion(entity.getFechaExpiracion())
                .revocado(entity.isRevocado())
                .ipOrigen(entity.getIpOrigen())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken domain, UsuarioEntity usuarioEntity) {
        if (domain == null) return null;
        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .usuario(usuarioEntity)
                .token(domain.getToken())
                .fechaExpiracion(domain.getFechaExpiracion())
                .revocado(domain.isRevocado())
                .ipOrigen(domain.getIpOrigen())
                .userAgent(domain.getUserAgent())
                .revokedAt(domain.getRevokedAt())
                .build();
    }
}
