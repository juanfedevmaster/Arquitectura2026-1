package com.juanfedevmaster.sangabriel.sangabrielwebapi.application.mapper;

import com.juanfedevmaster.sangabriel.sangabrielwebapi.application.dto.AuditoriaAutenticacionMensaje;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;
import org.springframework.stereotype.Component;

/**
 * Implementación del patrón Adapter.
 * Adapta el mensaje completo recibido del broker (Adaptee)
 * al modelo de dominio reducido (Target).
 */
@Component
public class AuditoriaAdapterImpl implements AuditoriaAdapter {

    @Override
    public AuditoriaAutenticacion adaptar(AuditoriaAutenticacionMensaje mensaje) {
        return AuditoriaAutenticacion.builder()
                .id(mensaje.getId())
                .usernameIntentado(mensaje.getUsernameIntentado())
                .evento(mensaje.getEvento())
                .descripcion(mensaje.getDescripcion())
                .fechaEvento(mensaje.getFechaEvento())
                .exitoso(mensaje.isExitoso())
                .build();
    }
}
