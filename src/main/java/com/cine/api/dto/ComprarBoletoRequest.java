package com.cine.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ComprarBoletoRequest {

    @NotNull(message = "Debe seleccionar una funcion")
    @Positive(message = "La funcion seleccionada no es valida")
    @Schema(example = "1", description = "Identificador de la funcion disponible")
    private Long funcionId;

    @NotNull(message = "Debe seleccionar un asiento")
    @Positive(message = "El asiento debe ser mayor a cero")
    @Schema(example = "25", description = "Numero de asiento solicitado")
    private Integer asiento;

    public Long getFuncionId() {
        return funcionId;
    }

    public void setFuncionId(Long funcionId) {
        this.funcionId = funcionId;
    }

    public Integer getAsiento() {
        return asiento;
    }

    public void setAsiento(Integer asiento) {
        this.asiento = asiento;
    }
}
