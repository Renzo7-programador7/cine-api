package com.cine.api.exception;

import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Schema(description = "Respuesta estándar de error de la API")
public class ApiErrorResponse {

    @Schema(example = "VALIDATION_ERROR")
    private final String code;
    @Schema(example = "Existen errores de validación")
    private final String message;
    private final LocalDateTime timestamp;
    @Schema(description = "Errores agrupados por campo")
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
