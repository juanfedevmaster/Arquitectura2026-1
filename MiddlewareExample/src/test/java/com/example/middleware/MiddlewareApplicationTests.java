package com.example.middleware;

import com.example.middleware.model.NotificationChannel;
import com.example.middleware.model.NotificationEvent;
import com.example.middleware.model.TransformedMessage;
import com.example.middleware.service.MessageRouterService;
import com.example.middleware.service.MessageTransformerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MiddlewareApplicationTests {

    @Autowired
    private MessageRouterService routerService;

    @Autowired
    private MessageTransformerService transformerService;

    // ── Context ───────────────────────────────────────────────────────────────

    @Test
    void contextLoads() {}

    // ── MessageRouterService ──────────────────────────────────────────────────

    @Test
    void ecommercePurchaseRoutesToEmailAndPush() {
        NotificationEvent event = new NotificationEvent(
                "ecommerce", "purchase", "user123", Map.of("amount", 99.99));

        List<NotificationChannel> channels = routerService.route(event);

        assertThat(channels).containsExactlyInAnyOrder(
                NotificationChannel.EMAIL, NotificationChannel.PUSH);
    }

    @Test
    void bankTransferRoutesToEmailAndSms() {
        NotificationEvent event = new NotificationEvent(
                "bank", "transfer", "user456", Map.of("amount", 500.0));

        List<NotificationChannel> channels = routerService.route(event);

        assertThat(channels).containsExactlyInAnyOrder(
                NotificationChannel.EMAIL, NotificationChannel.SMS);
    }

    @Test
    void mobileLoginRoutesToPushAndSms() {
        NotificationEvent event = new NotificationEvent(
                "mobile", "login", "user789", Map.of("location", "Bogotá"));

        List<NotificationChannel> channels = routerService.route(event);

        assertThat(channels).containsExactlyInAnyOrder(
                NotificationChannel.PUSH, NotificationChannel.SMS);
    }

    @Test
    void unknownEventDefaultsToEmail() {
        NotificationEvent event = new NotificationEvent(
                "unknown", "unknown-type", "user000", Map.of());

        List<NotificationChannel> channels = routerService.route(event);

        assertThat(channels).containsExactly(NotificationChannel.EMAIL);
    }

    // ── MessageTransformerService ─────────────────────────────────────────────

    @Test
    void emailTransformContainsHtml() {
        NotificationEvent event = new NotificationEvent(
                "ecommerce", "purchase", "user123", Map.of("amount", 99.99));

        TransformedMessage msg = transformerService.transform(event, NotificationChannel.EMAIL);

        assertThat(msg.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(msg.getBody()).contains("<html>", "user123");
        assertThat(msg.getRecipient()).endsWith("@example.com");
    }

    @Test
    void smsTransformIsPlainText() {
        NotificationEvent event = new NotificationEvent(
                "bank", "transfer", "user456", Map.of("amount", 500.0));

        TransformedMessage msg = transformerService.transform(event, NotificationChannel.SMS);

        assertThat(msg.getChannel()).isEqualTo(NotificationChannel.SMS);
        assertThat(msg.getBody()).doesNotContain("<html>");
        assertThat(msg.getRecipient()).startsWith("+57");
    }

    @Test
    void pushTransformHasDeviceToken() {
        NotificationEvent event = new NotificationEvent(
                "mobile", "login", "user789", Map.of("location", "Bogotá"));

        TransformedMessage msg = transformerService.transform(event, NotificationChannel.PUSH);

        assertThat(msg.getChannel()).isEqualTo(NotificationChannel.PUSH);
        assertThat(msg.getRecipient()).startsWith("device-token-");
    }
}
