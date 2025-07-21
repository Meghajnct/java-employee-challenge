package com.reliaquest.api.exception;

import java.time.LocalDateTime;

/**
 * Standard error response format for the API.
 */
public record ErrorResponse(
    String timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this(String.valueOf(timestamp), status, error, message, path);
    }
}
