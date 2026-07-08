package com.cine.api.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class ApiErrorResponse {

    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, List<String>> errors;

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = Map.of();
    }

    public ApiErrorResponse(String code, String message, Map<String, List<String>> errors) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}