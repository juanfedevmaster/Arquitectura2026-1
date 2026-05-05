package com.example.middleware.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad de desarrollo para generar tokens JWT de prueba.
 *
 * Ejecución:
 *   mvn exec:java -Dexec.mainClass="com.example.middleware.util.JwtTokenGenerator"
 *
 * El token generado se usa como: Authorization: Bearer <token>
 */
public class JwtTokenGenerator {

    private static final String SECRET =
            "middleware-super-secret-key-must-be-at-least-32-characters-long";

    public static void main(String[] args) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String[] sources = {"ecommerce-system", "bank-system", "mobile-app"};

        for (String source : sources) {
            String token = Jwts.builder()
                    .subject(source)
                    .claim("source", source)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86_400_000L)) // 24h
                    .signWith(key)
                    .compact();

            System.out.println("=== " + source + " ===");
            System.out.println("Authorization: Bearer " + token);
            System.out.println();
        }
    }
}
