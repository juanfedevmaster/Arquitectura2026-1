package com.example.middleware.service;

import com.example.middleware.channel.ChannelSender;
import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.TransformedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Dispatcher Service
 * Envía el mensaje transformado al canal externo de forma asíncrona.
 * Usa el ThreadPool "notificationTaskExecutor" (AsyncConfig).
 * Los ChannelSender concretos son inyectados automáticamente por Spring.
 */
@Service
public class DispatcherService {

    private static final Logger log = LoggerFactory.getLogger(DispatcherService.class);

    private final Map<NotificationChannel, ChannelSender> senders;

    public DispatcherService(List<ChannelSender> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(ChannelSender::getChannel, Function.identity()));
        log.info("DispatcherService inicializado con canales: {}", senders.keySet());
    }

    /**
     * Despacha el mensaje de forma asíncrona (@Async).
     * Cada llamada se ejecuta en un hilo del ThreadPool dedicado,
     * por lo que un canal lento no bloquea a los demás.
     */
    @Async("notificationTaskExecutor")
    public void dispatch(TransformedMessage message) {
        ChannelSender sender = senders.get(message.getChannel());
        if (sender == null) {
            log.error("Sin sender registrado para canal: {}", message.getChannel());
            return;
        }
        try {
            sender.send(message);
            log.info("Despacho OK — canal={}, recipient={}",
                     message.getChannel(), message.getRecipient());
        } catch (Exception ex) {
            log.error("Error despachando via {} a {}: {}",
                      message.getChannel(), message.getRecipient(), ex.getMessage());
        }
    }
}
