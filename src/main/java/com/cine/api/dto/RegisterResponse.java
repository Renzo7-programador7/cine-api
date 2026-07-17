package com.cine.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirmación del registro público; no crea una sesión ni devuelve un JWT")
public record RegisterResponse(
        @Schema(example = "Usuario registrado correctamente") String message,
        @Schema(example = "cliente@cinegest.com") String email) {
}
