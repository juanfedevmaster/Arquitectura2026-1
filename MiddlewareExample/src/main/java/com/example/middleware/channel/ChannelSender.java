package com.example.middleware.channel;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.TransformedMessage;

/**
 * Contrato que debe cumplir cada sender de canal externo.
 * Implementaciones: EmailChannelSender, SmsChannelSender, PushChannelSender.
 */
public interface ChannelSender {

    /** Canal que gestiona este sender. */
    NotificationChannel getChannel();

    /** Envía el mensaje al servicio externo correspondiente. */
    void send(TransformedMessage message);
}
