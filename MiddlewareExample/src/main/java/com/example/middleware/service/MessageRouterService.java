package com.example.middleware.service;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Message Router Service — Strategy Pattern
 * Decide qué canal(es) de notificación corresponden a cada tipo de evento.
 *
 * Tabla de enrutamiento:
 *   ecommerce:purchase → EMAIL + PUSH
 *   bank:transfer      → EMAIL + SMS
 *   mobile:login       → PUSH  + SMS
 *
 * Usa @ConditionalOnProperty para que la tabla sea fácilmente
 * reemplazable en entornos específicos (ver application.yml).
 */
@Service
public class MessageRouterService {

    private static final Logger log = LoggerFactory.getLogger(MessageRouterService.class);

    private static final Map<String, List<NotificationChannel>> ROUTING_TABLE = Map.of(
        "ecommerce:purchase", List.of(NotificationChannel.EMAIL, NotificationChannel.PUSH),
        "bank:transfer",      List.of(NotificationChannel.EMAIL, NotificationChannel.SMS),
        "mobile:login",       List.of(NotificationChannel.PUSH,  NotificationChannel.SMS)
    );

    /**
     * Resuelve los canales destino para el evento dado.
     * Si no hay regla definida, cae a EMAIL por defecto.
     */
    public List<NotificationChannel> route(NotificationEvent event) {
        String key = event.getSource().toLowerCase() + ":" + event.getType().toLowerCase();
        List<NotificationChannel> channels = ROUTING_TABLE.getOrDefault(key,
                                                List.of(NotificationChannel.EMAIL));
        log.debug("Route key='{}' → channels={}", key, channels);
        return channels;
    }
}
