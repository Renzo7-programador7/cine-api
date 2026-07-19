package com.cine.api.entity;

import com.cine.api.validation.UniqueEmail;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@Schema(description = "Usuario administrado internamente. El registro público utiliza RegisterRequest.")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @UniqueEmail
    @Size(max = 254, message = "El email no puede superar los 254 caracteres")
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    @Schema(format = "password", accessMode = Schema.AccessMode.WRITE_ONLY, minLength = 6, maxLength = 72)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "(?i)^(ADMIN|USER)$", message = "El rol debe ser ADMIN o USER")
    @Schema(allowableValues = {"USER", "ADMIN"}, example = "USER")
    private String rol; // "ADMIN" o "USER"
}
