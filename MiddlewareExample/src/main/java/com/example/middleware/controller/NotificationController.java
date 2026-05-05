package com.example.middleware.controller;

import com.example.middleware.model.ApiResponse;
import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.NotificationEvent;
import com.example.middleware.model.TransformedMessage;
import com.example.middleware.service.DispatcherService;
import com.example.middleware.service.EventValidatorService;
import com.example.middleware.service.MessageRouterService;
import com.example.middleware.service.MessageTransformerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API Receiver — POST /api/events
 * Punto de entrada HTTP/REST del middleware.
 * Orquesta el pipeline: Validar → Enrutar → Transformar → Despachar.
 */
@Tag(name = "Eventos", description = "Recepción y procesamiento de eventos de sistemas externos")
@RestController
@RequestMapping("/api/events")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final EventValidatorService   eventValidator;
    private final MessageRouterService    messageRouter;
    private final MessageTransformerService messageTransformer;
    private final DispatcherService       dispatcher;

    public NotificationController(EventValidatorService eventValidator,
                                  MessageRouterService messageRouter,
                                  MessageTransformerService messageTransformer,
                                  DispatcherService dispatcher) {
        this.eventValidator      = eventValidator;
        this.messageRouter       = messageRouter;
        this.messageTransformer  = messageTransformer;
        this.dispatcher          = dispatcher;
    }

    @Operation(
        summary = "Recibir evento de un sistema externo",
        description = """
            Procesa un evento generado por E-commerce, Banco o App Móvil.
            El middleware valida el JWT, enruta a los canales correspondientes,
            transforma el mensaje y lo despacha de forma asíncrona.
            
            **Tabla de enrutamiento:**
            | source | type | Canales |
            |---|---|---|
            | ecommerce | purchase | EMAIL + PUSH |
            | bank | transfer | EMAIL + SMS |
            | mobile | login | PUSH + SMS |
            | *otros* | *otros* | EMAIL |
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202",
            description = "Evento aceptado y canales notificados",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Body inválido (falta campo requerido)",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
            description = "JWT ausente o inválido",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse> receiveEvent(
            @Parameter(description = "JWT Bearer token del sistema productor", required = true,
                       example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Evento generado por el sistema externo",
                content = @Content(examples = {
                    @ExampleObject(name = "Compra E-commerce",
                        value = """
                            {"source":"ecommerce","type":"purchase","userId":"user123",
                             "payload":{"amount":99.99,"product":"Laptop"}}"""),
                    @ExampleObject(name = "Transferencia Banco",
                        value = """
                            {"source":"bank","type":"transfer","userId":"user456",
                             "payload":{"amount":500.0,"destination":"ACC-789"}}"""),
                    @ExampleObject(name = "Login App Móvil",
                        value = """
                            {"source":"mobile","type":"login","userId":"user789",
                             "payload":{"location":"Bogotá","ip":"192.168.1.1"}}""")
                })
            )
            @Valid @RequestBody NotificationEvent event) {

        log.info("Evento recibido — source='{}', type='{}', userId='{}'",
                 event.getSource(), event.getType(), event.getUserId());

        // 1. Validar JWT + esquema
        eventValidator.validateToken(authHeader);

        // 2. Enrutar → decidir canal(es) destino
        List<NotificationChannel> channels = messageRouter.route(event);
        log.info("Canales resueltos para {}/{}: {}", event.getSource(), event.getType(), channels);

        // 3. Transformar + despachar de forma asíncrona por cada canal
        for (NotificationChannel channel : channels) {
            TransformedMessage message = messageTransformer.transform(event, channel);
            dispatcher.dispatch(message);
        }

        return ResponseEntity.accepted()
                .body(new ApiResponse(true,
                        "Evento procesado. Canales notificados: " + channels));
    }
}
