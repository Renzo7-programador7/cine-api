package com.cine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Sesión creada después de un inicio de sesión correcto")
public class LoginResponse {
    @Schema(description = "Token JWT que debe enviarse como Bearer token en endpoints protegidos")
    private String token;
    @Schema(example = "cliente@cinegest.com")
    private String email;
    @Schema(description = "Rol efectivo del usuario", allowableValues = {"USER", "ADMIN"}, example = "USER")
    private String rol;
    @Schema(description = "Nombre del usuario", example = "Ana Torres")
    private String usuario;
}
