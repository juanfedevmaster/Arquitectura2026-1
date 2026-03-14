package com.juanfedevmaster.sangabriel.authentication.domain.port.out;

import com.juanfedevmaster.sangabriel.authentication.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revocarTodosLosTokensDeUsuario(Long usuarioId);
}
