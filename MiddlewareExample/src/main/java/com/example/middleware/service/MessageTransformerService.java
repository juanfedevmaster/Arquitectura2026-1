package com.example.middleware.service;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.NotificationEvent;
import com.example.middleware.model.TransformedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Message Transformer Service
 * Formatea el mensaje según el canal destino usando Jackson ObjectMapper.
 * EMAIL → HTML enriquecido
 * SMS   → texto plano corto (límite de caracteres)
 * PUSH  → título + cuerpo breve
 */
@Component
public class MessageTransformerService {

    private final ObjectMapper objectMapper;

    public MessageTransformerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TransformedMessage transform(NotificationEvent event, NotificationChannel channel) {
        String subject   = buildSubject(event);
        String body      = buildBody(event, channel);
        String recipient = resolveRecipient(event, channel);
        return new TransformedMessage(channel, recipient, subject, body);
    }

    // ── Sujeto ────────────────────────────────────────────────────────────────

    private String buildSubject(NotificationEvent event) {
        return switch (event.getType().toLowerCase()) {
            case "purchase" -> "Compra realizada exitosamente";
            case "transfer" -> "Transferencia bancaria confirmada";
            case "login"    -> "Nuevo inicio de sesión detectado";
            default         -> "Notificación del sistema";
        };
    }

    // ── Cuerpo por canal ──────────────────────────────────────────────────────

    private String buildBody(NotificationEvent event, NotificationChannel channel) {
        String baseMsg = buildBaseMessage(event);
        return switch (channel) {
            case EMAIL -> buildEmailBody(event, baseMsg);
            case SMS   -> baseMsg + ". Usuario: " + event.getUserId();
            case PUSH  -> baseMsg;
        };
    }

    private String buildBaseMessage(NotificationEvent event) {
        return switch (event.getType().toLowerCase()) {
            case "purchase" -> "Se realizó una compra en " + event.getSource();
            case "transfer" -> "Se procesó una transferencia bancaria";
            case "login"    -> "Se detectó un inicio de sesión desde un lugar nuevo";
            default         -> "Evento generado por " + event.getSource();
        };
    }

    private String buildEmailBody(NotificationEvent event, String baseMsg) {
        return "<html><body>" +
               "<h2>" + buildSubject(event) + "</h2>" +
               "<p>" + baseMsg + "</p>" +
               "<p><strong>Usuario:</strong> " + event.getUserId() + "</p>" +
               "<p><strong>Fuente:</strong> " + event.getSource() + "</p>" +
               "<hr/><small>Middleware de Notificaciones — " +
               java.time.Instant.now() + "</small>" +
               "</body></html>";
    }

    // ── Destinatario ──────────────────────────────────────────────────────────
    // En producción estos datos se resolverían desde un servicio de perfiles.

    private String resolveRecipient(NotificationEvent event, NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> event.getUserId() + "@example.com";
            case SMS   -> "+57-300-" + Math.abs(event.getUserId().hashCode() % 10000000);
            case PUSH  -> "device-token-" + event.getUserId();
        };
    }
}
