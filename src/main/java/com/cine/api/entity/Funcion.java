package com.cine.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Future(message = "La fecha debe posterior a la fecha actual")
    private LocalDate fecha;
    @FutureOrPresent(message = "La hora debe ser igual o posterior a la hora actual")
    private LocalTime hora;
    @NotBlank(message = "El precio es obligatorio")
    @DecimalMax(value = "100.0", message = "El precio no puede ser mayor a 100")
    @Positive(message = "El precio debe ser un valor positivo")
    private double precio;
    @NotBlank(message = "La sala debe ser especificada")
    @DecimalMax(value = "1000", message = "La capacidad no puede ser mayor a 1000")
    @Positive(message = "La capacidad debe ser un valor positivo")
    private int capacidad;

    @ManyToOne
    private Pelicula pelicula;
}