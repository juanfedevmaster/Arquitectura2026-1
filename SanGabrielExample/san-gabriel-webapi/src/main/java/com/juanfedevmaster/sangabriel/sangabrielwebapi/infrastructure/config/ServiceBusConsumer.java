package com.juanfedevmaster.sangabriel.sangabrielwebapi.infrastructure.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.core.amqp.AmqpTransportType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.application.dto.AuditoriaAutenticacionMensaje;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.application.mapper.AuditoriaAdapter;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.application.usecase.ProcesarAuditoriaUseCase;
import com.juanfedevmaster.sangabriel.sangabrielwebapi.domain.model.AuditoriaAutenticacion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Adaptador de entrada (input adapter) en la arquitectura hexagonal.
 * Consume mensajes del tópico de Azure Service Bus y los delega
 * al caso de uso correspondiente a través del patrón Adapter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceBusConsumer {

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.topic-name}")
    private String topicName;

    @Value("${azure.servicebus.subscription-name}")
    private String subscriptionName;

    private final AuditoriaAdapter auditoriaAdapter;
    private final ProcesarAuditoriaUseCase procesarAuditoriaUseCase;
    private final ObjectMapper objectMapper;

    private ServiceBusProcessorClient processorClient;

    @PostConstruct
    public void iniciar() {
        processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                .processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .processMessage(this::procesarMensaje)
                .processError(this::procesarError)
                .buildProcessorClient();

        processorClient.start();
        log.info("Service Bus consumer iniciado — tópico: '{}', suscripción: '{}'", topicName, subscriptionName);
    }

    @PreDestroy
    public void detener() {
        if (processorClient != null) {
            processorClient.close();
            log.info("Service Bus consumer detenido.");
        }
    }

    private void procesarMensaje(ServiceBusReceivedMessageContext context) {
        try {
            String body = new String(context.getMessage().getBody().toBytes(), StandardCharsets.UTF_8);
            log.info("Mensaje recibido del tópico '{}': {}", topicName, body);

            AuditoriaAutenticacionMensaje mensaje = objectMapper.readValue(body, AuditoriaAutenticacionMensaje.class);
            AuditoriaAutenticacion auditoria = auditoriaAdapter.adaptar(mensaje);
            procesarAuditoriaUseCase.ejecutar(auditoria);

            context.complete();
        } catch (Exception e) {
            log.error("Error al procesar mensaje de Service Bus: {}", e.getMessage(), e);
            context.abandon();
        }
    }

    private void procesarError(ServiceBusErrorContext context) {
        log.error("Error en Service Bus [fuente: {}, entidad: {}, namespace: {}]: {}",
                context.getErrorSource(),
                context.getEntityPath(),
                context.getFullyQualifiedNamespace(),
                context.getException().getMessage(),
                context.getException());
    }
}
