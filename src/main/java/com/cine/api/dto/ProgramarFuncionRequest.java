package com.cine.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "Datos requeridos para programar una funcion cinematografica")
public class ProgramarFuncionRequest {

    @NotNull(message = "La fecha es obligatoria")
    @Schema(example = "2026-07-25")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Schema(example = "19:30")
    private LocalTime hora;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    @DecimalMax(value = "100.0", message = "El precio no puede ser mayor a 100")
    @Schema(example = "20.00")
    private Double precio;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser un valor positivo")
    @DecimalMax(value = "1000", message = "La capacidad no puede ser mayor a 1000")
    @Schema(example = "120")
    private Integer capacidad;

    @NotNull(message = "La pelicula es obligatoria")
    @Positive(message = "La pelicula seleccionada no es valida")
    @Schema(example = "1")
    private Long peliculaId;
}
