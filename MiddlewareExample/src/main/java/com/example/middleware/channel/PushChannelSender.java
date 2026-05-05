package com.example.middleware.channel;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.TransformedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Push Channel Sender — Firebase FCM / APNs
 * En producción se reemplaza el cuerpo de send() por una llamada
 * al SDK de Firebase Admin o a la API de APNs.
 */
@Component
public class PushChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(PushChannelSender.class);

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public void send(TransformedMessage message) {
        // TODO en producción: firebaseMessaging.send(Message.builder()...)
        log.info("[PUSH → Firebase] Device: {} | Title: {} | Body: {}",
                 message.getRecipient(), message.getSubject(), message.getBody());
    }
}
