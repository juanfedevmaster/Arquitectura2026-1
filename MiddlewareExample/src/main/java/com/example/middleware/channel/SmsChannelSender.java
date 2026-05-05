package com.example.middleware.channel;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.TransformedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SMS Channel Sender — Twilio / AWS SNS
 * En producción se reemplaza el cuerpo de send() por una llamada
 * a la API de Twilio o al SDK de AWS SNS.
 */
@Component
public class SmsChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(SmsChannelSender.class);

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(TransformedMessage message) {
        // TODO en producción: twilioClient.messages().create(message)
        log.info("[SMS → Twilio] To: {} | Body: {}",
                 message.getRecipient(), message.getBody());
    }
}
