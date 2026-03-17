package com.juanfedevmaster.sangabriel.authentication.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.juanfedevmaster.sangabriel.authentication.domain.model.AuditoriaAutenticacion;
import com.juanfedevmaster.sangabriel.authentication.domain.port.out.EventoAutenticacionPublisherPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceBusEventoPublisher implements EventoAutenticacionPublisherPort {

    private final ServiceBusSenderClient senderClient;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    public void publicar(AuditoriaAutenticacion auditoria) {
        try {
            String body = objectMapper.writeValueAsString(auditoria);
            senderClient.sendMessage(new ServiceBusMessage(body));
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar evento", e);
        }
    }
}
