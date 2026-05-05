package com.example.middleware.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Evento generado por un sistema externo (E-commerce, Banco, App Móvil).
 * Enviado via POST /api/events con JWT en el header Authorization.
 */
@Schema(description = "Evento generado por un sistema externo")
public class NotificationEvent {

    @Schema(description = "Sistema que genera el evento", example = "ecommerce",
            allowableValues = {"ecommerce", "bank", "mobile"})
    @NotBlank(message = "source es requerido (ecommerce | bank | mobile)")
    private String source;

    @Schema(description = "Tipo de evento", example = "purchase",
            allowableValues = {"purchase", "transfer", "login"})
    @NotBlank(message = "type es requerido (purchase | transfer | login)")
    private String type;

    @Schema(description = "Identificador del usuario afectado", example = "user123")
    @NotBlank(message = "userId es requerido")
    private String userId;

    @Schema(description = "Datos adicionales del evento", example = "{\"amount\": 99.99}")
    @NotNull(message = "payload no puede ser nulo")
    private Map<String, Object> payload;

    public NotificationEvent() {}

    public NotificationEvent(String source, String type, String userId, Map<String, Object> payload) {
        this.source  = source;
        this.type    = type;
        this.userId  = userId;
        this.payload = payload;
    }

    public String getSource()                         { return source; }
    public void setSource(String source)              { this.source = source; }

    public String getType()                           { return type; }
    public void setType(String type)                  { this.type = type; }

    public String getUserId()                         { return userId; }
    public void setUserId(String userId)              { this.userId = userId; }

    public Map<String, Object> getPayload()           { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "NotificationEvent{source='" + source + "', type='" + type + "', userId='" + userId + "'}";
    }
}
