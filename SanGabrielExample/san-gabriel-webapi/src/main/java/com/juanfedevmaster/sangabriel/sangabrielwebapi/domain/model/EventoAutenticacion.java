package com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model;

public enum EventoAutenticacion {

    LOGIN_EXITOSO,
    LOGIN_FALLIDO,
    LOGOUT,
    REGISTRO,
    CAMBIO_PASSWORD,
    BLOQUEO_CUENTA,
    DESBLOQUEO_CUENTA,
    REFRESH_TOKEN,
    TOKEN_REVOCADO,
    ACCESO_DENEGADO
}
