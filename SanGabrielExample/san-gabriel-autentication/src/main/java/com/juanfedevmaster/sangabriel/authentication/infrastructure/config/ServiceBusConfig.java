package com.juanfedevmaster.sangabriel.authentication.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

@Configuration
public class ServiceBusConfig {

    @Bean
    public ServiceBusSenderClient serviceBusSenderClient(ServiceBusProperties props) {
        return new ServiceBusClientBuilder()
                .connectionString(props.getConnectionString())
                .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                .sender()
                .topicName(props.getTopicName())
                .buildClient();
    }
}
