package com.juanfedevmaster.sangabriel.authentication.domain.port.out;

import com.juanfedevmaster.sangabriel.authentication.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Usuario save(Usuario usuario);
}
