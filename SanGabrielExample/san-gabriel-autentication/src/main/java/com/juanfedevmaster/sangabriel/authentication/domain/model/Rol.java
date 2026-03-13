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
public class Rol {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
