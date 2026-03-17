package com.juanfedevmaster.sangabriel.authentication.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "azure.servicebus")
@Getter @Setter
public class ServiceBusProperties {
    private String connectionString;
    private String topicName;
}
