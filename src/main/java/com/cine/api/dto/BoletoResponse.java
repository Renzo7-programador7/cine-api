package com.cine.api.dto;

import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;

public record BoletoResponse(
        Long id,
        double precio,
        String estado,
        int asiento,
        UsuarioResumen usuario,
        Funcion funcion) {

    public static BoletoResponse from(Boleto boleto) {
        return new BoletoResponse(
                boleto.getId(),
                boleto.getPrecio(),
                boleto.getEstado(),
                boleto.getAsiento(),
                new UsuarioResumen(
                        boleto.getUsuario().getId(),
                        boleto.getUsuario().getNombre(),
                        boleto.getUsuario().getEmail()),
                boleto.getFuncion());
    }

    public record UsuarioResumen(Long id, String nombre, String email) {}
}
