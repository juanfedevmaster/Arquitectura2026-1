package com.juanfedevmaster.sangabriel.authentication.adapter.in.web;

import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginRequestDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginResponseDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.RefreshTokenRequestDTO;
import com.juanfedevmaster.sangabriel.authentication.domain.port.in.AutenticacionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y gestión de sesiones")
public class AuthController {

    private final AutenticacionUseCase autenticacionUseCase;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un access token y un refresh token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                   HttpServletRequest httpRequest) {
        String ipOrigen = obtenerIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        LoginResponseDTO response = autenticacionUseCase.login(request, ipOrigen, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Genera un nuevo access token usando un refresh token válido")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request,
                                                          HttpServletRequest httpRequest) {
        String ipOrigen = obtenerIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        LoginResponseDTO response = autenticacionUseCase.refreshToken(request, ipOrigen, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token del usuario")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        autenticacionUseCase.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
