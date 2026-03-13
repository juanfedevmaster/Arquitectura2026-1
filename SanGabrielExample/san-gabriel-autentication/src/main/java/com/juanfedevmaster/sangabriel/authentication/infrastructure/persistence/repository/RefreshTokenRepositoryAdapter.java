package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.authentication.domain.model.RefreshToken;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.UsuarioEntity;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        UsuarioEntity usuarioEntity = usuarioJpaRepository.findById(refreshToken.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + refreshToken.getUsuarioId()));
        var entity = refreshTokenMapper.toEntity(refreshToken, usuarioEntity);
        var saved = refreshTokenJpaRepository.save(entity);
        return refreshTokenMapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token).map(refreshTokenMapper::toDomain);
    }

    @Override
    public void revocarTodosLosTokensDeUsuario(Long usuarioId) {
        refreshTokenJpaRepository.revocarTodosLosTokensActivosPorUsuario(usuarioId);
    }
}
