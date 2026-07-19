package com.cine.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Locale;

@Data
@Schema(description = "Datos del registro público. El rol no forma parte de esta solicitud y siempre será USER.")
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del cliente", example = "Ana Torres", minLength = 2, maxLength = 100)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 254, message = "El email no puede superar los 254 caracteres")
    @Schema(description = "Correo único; se guarda en minúsculas", example = "ana@cinegest.com", maxLength = 254)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    @Schema(description = "Contraseña de 6 a 72 caracteres", example = "secreto123", format = "password", minLength = 6, maxLength = 72)
    private String password;

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
