package com.example.middleware.channel;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.TransformedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Email Channel Sender — SendGrid / SMTP
 * En producción se reemplaza el cuerpo de send() por una llamada
 * a la API de SendGrid o un JavaMailSender configurado en application.yml.
 */
@Component
public class EmailChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(EmailChannelSender.class);

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(TransformedMessage message) {
        // TODO en producción: sendGridClient.send(message)
        log.info("[EMAIL → SendGrid] To: {} | Subject: {}",
                 message.getRecipient(), message.getSubject());
        log.debug("[EMAIL] Body: {}", message.getBody());
    }
}
