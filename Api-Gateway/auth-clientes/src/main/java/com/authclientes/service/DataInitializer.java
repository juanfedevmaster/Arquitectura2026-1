package com.authclientes.service;

import com.authclientes.entity.Role;
import com.authclientes.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole("ROLE_USER");
        seedRole("ROLE_ADMIN");
    }

    private void seedRole(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(new Role(null, name));
            log.info("Role '{}' created.", name);
        }
    }
}
