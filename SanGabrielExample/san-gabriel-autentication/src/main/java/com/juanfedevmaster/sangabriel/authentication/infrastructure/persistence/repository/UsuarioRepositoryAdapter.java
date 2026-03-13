package com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository;

import com.juanfedevmaster.sangabriel.authentication.domain.model.Usuario;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.UsuarioRepositoryPort;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioJpaRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioJpaRepository.findByUsername(username).map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(usuarioMapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        var entity = usuarioMapper.toEntity(usuario);
        var saved = usuarioJpaRepository.save(entity);
        return usuarioMapper.toDomain(saved);
    }
}
