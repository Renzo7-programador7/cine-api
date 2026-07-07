package com.cine.api.exception;

import java.time.LocalDateTime;

public class ApiErrorResponse {

    private final String code;
    private final String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
