package com.example.middleware.model;

/**
 * Canales externos de notificación disponibles.
 * EMAIL → SendGrid / SMTP
 * SMS   → Twilio / AWS SNS
 * PUSH  → Firebase / APNs
 */
public enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH
}
