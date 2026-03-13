package com.juanfedevmaster.sangabriel.authentication.infrastructure.security;

import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.entity.UsuarioEntity;
import com.juanfedevmaster.sangabriel.authentication.infrastructure.persistence.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioJpaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<SimpleGrantedAuthority> authorities = usuario.getRoles() != null
                ? usuario.getRoles().stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                    .collect(Collectors.toList())
                : List.of();

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .accountExpired(usuario.isCuentaExpirada())
                .accountLocked(usuario.isCuentaBloqueada())
                .credentialsExpired(usuario.isCredencialesExpiradas())
                .disabled(!usuario.isEstado())
                .build();
    }
}
