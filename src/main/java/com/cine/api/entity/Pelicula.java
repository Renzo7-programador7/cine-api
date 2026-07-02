package com.cine.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 30, message = "La duración debe ser mayor a 30 minutos")
    private Integer duracion;
    @NotBlank(message = "La clasificación es obligatoria")
    private String clasificacion;
    @NotBlank(message = "El género es obligatorio")
    private String genero;
}