package com.example.middleware.model;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta estándar del API.
 */
public class ApiResponse {

    private final boolean success;
    private final String  message;
    private final Instant timestamp;

    public ApiResponse(boolean success, String message) {
        this.success   = success;
        this.message   = message;
        this.timestamp = Instant.now();
    }

    public boolean isSuccess()   { return success; }
    public String  getMessage()  { return message; }
    public Instant getTimestamp(){ return timestamp; }
}
