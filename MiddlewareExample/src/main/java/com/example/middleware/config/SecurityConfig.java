package com.example.middleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 * La validación JWT se delega a EventValidatorService dentro del pipeline,
 * por lo que el endpoint /api/events se permite aquí pero es protegido
 * lógicamente por el validador.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/prometheus", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**").permitAll()
                .requestMatchers("/api/events").permitAll()  // JWT validado por EventValidatorService
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
