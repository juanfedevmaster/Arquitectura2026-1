package com.juanfedevmaster.sangabriel.sangabrielwebapi.application.mapper;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.application.dto.AuditoriaAutenticacionMensaje;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;

/**
 * Interfaz del patrón Adapter.
 * Define el contrato para convertir el mensaje completo del broker
 * en el modelo de dominio reducido.
 */
public interface AuditoriaAdapter {

    AuditoriaAutenticacion adaptar(AuditoriaAutenticacionMensaje mensaje);
}
