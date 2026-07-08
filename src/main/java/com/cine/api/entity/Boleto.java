package com.cine.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "El precio debe ser un valor positivo")
    @DecimalMax(value = "1000.0", message = "El precio no puede ser mayor a 1000.0")
    private double precio;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
    @Positive(message = "El asiento debe ser un valor positivo")
    @DecimalMax(value = "1000", message = "El asiento no puede ser mayor a 1000")
    private int asiento;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Funcion funcion;
}