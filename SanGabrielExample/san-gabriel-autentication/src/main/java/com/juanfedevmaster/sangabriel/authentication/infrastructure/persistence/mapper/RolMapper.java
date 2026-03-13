package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.mapper;

import com.juanfedevmaster.sangabriel.authentication.domain.model.Rol;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.RolEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RolMapper {

    public Rol toDomain(RolEntity entity) {
        if (entity == null) return null;
        return Rol.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .estado(entity.isEstado())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<Rol> toDomainList(List<RolEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
