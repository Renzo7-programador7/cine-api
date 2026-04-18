package com.cine.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double precio;
    private String estado;
    private int asiento;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Funcion funcion;
}