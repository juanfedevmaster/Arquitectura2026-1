package com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.repository;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;

/**
 * Puerto (port) de salida en la arquitectura hexagonal.
 * Define el contrato de persistencia para el dominio,
 * sin acoplamiento a ninguna tecnología de base de datos.
 */
public interface AuditoriaAutenticacionRepository {

    void guardar(AuditoriaAutenticacion auditoria);
}
