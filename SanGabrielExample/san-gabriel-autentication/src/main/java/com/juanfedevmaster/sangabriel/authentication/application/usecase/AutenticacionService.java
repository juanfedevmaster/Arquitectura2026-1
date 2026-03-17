package com.juanfedevmaster.sangabriel.authentication.application.usecase;

import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginRequestDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.LoginResponseDTO;
import com.juanfedevmaster.sangabriel.authentication.application.dto.RefreshTokenRequestDTO;
import com.juanfedevmaster.sangabriel.authentication.domain.exception.CuentaBloqueadaException;
import com.juanfedevmaster.sangabriel.authentication.domain.exception.CredencialesInvalidasException;
import com.juanfedevmaster.sangabriel.authentication.domain.exception.RefreshTokenInvalidoException;
import com.juanfedevmaster.sangabriel.authentication.domain.model.AuditoriaAutenticacion;
import com.juanfedevmaster.sangabriel.authentication.domain.model.EventoAutenticacion;
import com.juanfedevmaster.sangabriel.authentication.domain.model.RefreshToken;
import com.juanfedevmaster.sangabriel.authentication.domain.model.Usuario;
import com.juanfedevmaster.sangabriel.authentication.domain.port.in.AutenticacionUseCase;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.AuditoriaRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.EventoAutenticacionPublisherPort;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.UsuarioRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.config.JwtProperties;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.config.SecurityProperties;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutenticacionService implements AutenticacionUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final AuditoriaRepositoryPort auditoriaRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final SecurityProperties securityProperties;
    private final EventoAutenticacionPublisherPort eventoPublisherPort;


    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, String ipOrigen, String userAgent) {
        Usuario usuario = usuarioRepositoryPort.findByUsername(request.getUsername())
                .orElse(null);

        // Usuario no encontrado: registrar intento fallido sin revelar si existe
        if (usuario == null) {
            registrarAuditoria(null, request.getUsername(),
                    EventoAutenticacion.LOGIN_FALLIDO, "Usuario no encontrado", ipOrigen, userAgent, false);
            throw new CredencialesInvalidasException("Credenciales incorrectas");
        }

        // Cuenta bloqueada
        if (usuario.isCuentaBloqueada()) {
            registrarAuditoria(usuario.getId(), usuario.getUsername(),
                    EventoAutenticacion.LOGIN_FALLIDO, "Cuenta bloqueada", ipOrigen, userAgent, false);
            throw new CuentaBloqueadaException("La cuenta está bloqueada. Contacte al administrador.");
        }

        // Cuenta inactiva
        if (!usuario.isEstado()) {
            registrarAuditoria(usuario.getId(), usuario.getUsername(),
                    EventoAutenticacion.LOGIN_FALLIDO, "Cuenta inactiva", ipOrigen, userAgent, false);
            throw new CredencialesInvalidasException("Credenciales incorrectas");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            int intentos = usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(intentos);

            if (intentos >= securityProperties.getMaxIntentosFallidos()) {
                usuario.setCuentaBloqueada(true);
                usuario.setUpdatedAt(LocalDateTime.now());
                usuarioRepositoryPort.save(usuario);
                registrarAuditoria(usuario.getId(), usuario.getUsername(),
                        EventoAutenticacion.BLOQUEO_CUENTA,
                        "Cuenta bloqueada por " + intentos + " intentos fallidos", ipOrigen, userAgent, false);
                throw new CuentaBloqueadaException("La cuenta ha sido bloqueada por demasiados intentos fallidos.");
            }

            usuario.setUpdatedAt(LocalDateTime.now());
            usuarioRepositoryPort.save(usuario);
            registrarAuditoria(usuario.getId(), usuario.getUsername(),
                    EventoAutenticacion.LOGIN_FALLIDO,
                    "Contraseña incorrecta. Intento " + intentos + " de " + securityProperties.getMaxIntentosFallidos(),
                    ipOrigen, userAgent, false);
            throw new CredencialesInvalidasException("Credenciales incorrectas");
        }

        // Login exitoso — resetear intentos y actualizar último login
        usuario.setIntentosFallidos(0);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());
        usuarioRepositoryPort.save(usuario);

        // Revocar refresh tokens anteriores del usuario
        refreshTokenRepositoryPort.revocarTodosLosTokensDeUsuario(usuario.getId());

        // Generar tokens
        String accessToken = jwtUtil.generateAccessToken(usuario);
        RefreshToken refreshToken = crearRefreshToken(usuario.getId(), ipOrigen, userAgent);

        registrarAuditoria(usuario.getId(), usuario.getUsername(),
                EventoAutenticacion.LOGIN_EXITOSO, "Login exitoso", ipOrigen, userAgent, true);

        return construirLoginResponse(accessToken, refreshToken.getToken(), usuario);
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request, String ipOrigen, String userAgent) {
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token no válido"));

        if (refreshToken.isRevocado()) {
            throw new RefreshTokenInvalidoException("El refresh token ha sido revocado");
        }

        if (refreshToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenInvalidoException("El refresh token ha expirado");
        }

        Usuario usuario = cargarUsuarioPorId(refreshToken.getUsuarioId());

        if (usuario == null || !usuario.isEstado() || usuario.isCuentaBloqueada()) {
            throw new CredencialesInvalidasException("El usuario no está disponible");
        }

        String nuevoAccessToken = jwtUtil.generateAccessToken(usuario);

        // Rotar el refresh token
        refreshToken.setRevocado(true);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepositoryPort.save(refreshToken);

        RefreshToken nuevoRefreshToken = crearRefreshToken(usuario.getId(), ipOrigen, userAgent);

        registrarAuditoria(usuario.getId(), usuario.getUsername(),
                EventoAutenticacion.REFRESH_TOKEN, "Token renovado", ipOrigen, userAgent, true);

        return construirLoginResponse(nuevoAccessToken, nuevoRefreshToken.getToken(), usuario);
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenRepositoryPort.findByToken(token).ifPresent(refreshToken -> {
            Long usuarioId = refreshToken.getUsuarioId();
            refreshToken.setRevocado(true);
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepositoryPort.save(refreshToken);

            Usuario usuario = cargarUsuarioPorId(usuarioId);
            String username = (usuario != null) ? usuario.getUsername() : "desconocido";
            registrarAuditoria(usuarioId, username,
                    EventoAutenticacion.LOGOUT, "Sesión cerrada", null, null, true);
        });
    }

    // -----------------------------------------------------------------------
    // Métodos privados de apoyo
    // -----------------------------------------------------------------------

    private RefreshToken crearRefreshToken(Long usuarioId, String ipOrigen, String userAgent) {
        RefreshToken refreshToken = RefreshToken.builder()
                .usuarioId(usuarioId)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .revocado(false)
                .ipOrigen(ipOrigen)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();
        return refreshTokenRepositoryPort.save(refreshToken);
    }

    private void registrarAuditoria(Long usuarioId, String usernameIntentado,
                                    EventoAutenticacion evento, String descripcion,
                                    String ipOrigen, String userAgent, boolean exitoso) {
        AuditoriaAutenticacion auditoria = AuditoriaAutenticacion.builder()
                .usuarioId(usuarioId)
                .usernameIntentado(usernameIntentado)
                .evento(evento)
                .descripcion(descripcion)
                .ipOrigen(ipOrigen)
                .userAgent(userAgent)
                .fechaEvento(LocalDateTime.now())
                .exitoso(exitoso)
                .build();
        auditoriaRepositoryPort.registrar(auditoria);
        eventoPublisherPort.publicar(auditoria);
    }

    private LoginResponseDTO construirLoginResponse(String accessToken, String refreshTokenStr, Usuario usuario) {
        List<String> roles = usuario.getRoles() != null
                ? usuario.getRoles().stream().map(r -> r.getNombre()).toList()
                : List.of();

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(roles)
                .build();
    }

    /**
     * Carga usuario por ID delegando al puerto de repositorio.
     */
    private Usuario cargarUsuarioPorId(Long usuarioId) {
        return usuarioRepositoryPort.findById(usuarioId).orElse(null);
    }
}
