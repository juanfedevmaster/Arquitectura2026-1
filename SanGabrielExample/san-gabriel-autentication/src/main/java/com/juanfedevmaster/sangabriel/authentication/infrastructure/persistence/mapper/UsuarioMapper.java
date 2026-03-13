package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.mapper;

import com.juanfedevmaster.sangabriel.authentication.domain.model.Usuario;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final RolMapper rolMapper;

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .tipoDocumento(entity.getTipoDocumento())
                .telefono(entity.getTelefono())
                .cargo(entity.getCargo())
                .area(entity.getArea())
                .estado(entity.isEstado())
                .cuentaBloqueada(entity.isCuentaBloqueada())
                .cuentaExpirada(entity.isCuentaExpirada())
                .credencialesExpiradas(entity.isCredencialesExpiradas())
                .intentosFallidos(entity.getIntentosFallidos())
                .ultimoLogin(entity.getUltimoLogin())
                .ultimoCambioPassword(entity.getUltimoCambioPassword())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .roles(rolMapper.toDomainList(entity.getRoles()))
                .build();
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        return UsuarioEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .nombres(domain.getNombres())
                .apellidos(domain.getApellidos())
                .documentoIdentidad(domain.getDocumentoIdentidad())
                .tipoDocumento(domain.getTipoDocumento())
                .telefono(domain.getTelefono())
                .cargo(domain.getCargo())
                .area(domain.getArea())
                .estado(domain.isEstado())
                .cuentaBloqueada(domain.isCuentaBloqueada())
                .cuentaExpirada(domain.isCuentaExpirada())
                .credencialesExpiradas(domain.isCredencialesExpiradas())
                .intentosFallidos(domain.getIntentosFallidos())
                .ultimoLogin(domain.getUltimoLogin())
                .ultimoCambioPassword(domain.getUltimoCambioPassword())
                .build();
    }
}
