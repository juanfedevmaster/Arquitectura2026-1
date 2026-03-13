package com.juanfedevmaster.sangabriel.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;
    private Long usuarioId;
    private String token;
    private LocalDateTime fechaExpiracion;
    private boolean revocado;
    private String ipOrigen;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;
}
