package com.juanfedevmaster.sangabriel.authentication.domain.port.in;

import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginRequestDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginResponseDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.RefreshTokenRequestDTO;

public interface AutenticacionUseCase {

    /**
     * Autentica un usuario con sus credenciales y retorna los tokens JWT.
     */
    LoginResponseDTO login(LoginRequestDTO request, String ipOrigen, String userAgent);

    /**
     * Renueva el access token a partir de un refresh token válido.
     */
    LoginResponseDTO refreshToken(RefreshTokenRequestDTO request, String ipOrigen, String userAgent);

    /**
     * Revoca el refresh token cerrando la sesión del usuario.
     */
    void logout(String refreshToken);
}
