package com.cine.api.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @DecimalMax(value = "100.0", message = "El precio no puede ser mayor a 100")
    @Positive(message = "El precio debe ser un valor positivo")
    private double precio;

    @DecimalMax(value = "1000", message = "La capacidad no puede ser mayor a 1000")
    @Positive(message = "La capacidad debe ser un valor positivo")
    private int capacidad;

    @ManyToOne
    @NotNull(message = "La pelicula es obligatoria")
    private Pelicula pelicula;
}
