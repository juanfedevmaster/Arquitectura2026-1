package com.example.middleware.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Event Validator Service
 * Valida el esquema del evento (JSR-380 via @Valid en el controller)
 * y autentica el JWT del header Authorization.
 */
@Service
public class EventValidatorService {

    private final SecretKey signingKey;

    public EventValidatorService(@Value("${middleware.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret debe tener al menos 32 caracteres");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Verifica que el header tenga un Bearer JWT válido y no expirado.
     *
     * @param authHeader valor del header "Authorization"
     * @return Claims extraídos del token
     * @throws ResponseStatusException 401 si el token es inválido o ausente
     */
    public Claims validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Se requiere header Authorization: Bearer <token>");
        }

        String token = authHeader.substring(7);
        try {
            return Jwts.parser()
                       .verifyWith(signingKey)
                       .build()
                       .parseSignedClaims(token)
                       .getPayload();
        } catch (JwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "JWT inválido o expirado: " + ex.getMessage());
        }
    }
}
