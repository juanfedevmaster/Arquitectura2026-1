package com.juanfedevmaster.sangabriel.authentication.infrastructure.security;

import com.juanfedevmaster.sangabriel.authentication.domain.model.Usuario;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(Usuario usuario) {
        List<String> roles = usuario.getRoles() != null
                ? usuario.getRoles().stream().map(r -> r.getNombre()).collect(Collectors.toList())
                : List.of();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("userId", usuario.getId())
                .claim("email", usuario.getEmail())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiration() {
        return jwtProperties.getExpiration();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
