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
public class UsuarioRol {

    private Long id;
    private Long usuarioId;
    private Long rolId;
    private LocalDateTime createdAt;
}
