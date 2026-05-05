package com.example.middleware.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de SpringDoc OpenAPI 3.
 * Swagger UI disponible en: http://localhost:8080/swagger-ui.html
 * JSON spec en:             http://localhost:8080/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI middlewareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Middleware de Notificaciones Multi-Canal")
                        .description("""
                                Sistema intermediario entre productores de eventos (E-commerce, Banco, App Móvil)
                                y canales externos de notificación (Email, SMS, Push).
                                
                                **Pipeline interno:** API Receiver → Event Validator → Message Router
                                → Message Transformer → Dispatcher
                                
                                **Autenticación:** JWT Bearer Token requerido en todos los endpoints `/api/**`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Arquitectura 2026-1")
                                .email("arquitectura@example.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT generado por JwtTokenGenerator. " +
                                                     "Formato: Bearer <token>")));
    }
}
