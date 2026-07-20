package com.cine.api.dto;

import java.util.List;

public record DisponibilidadAsientosResponse(
        Long funcionId,
        int capacidad,
        List<Integer> asientosOcupados) {}
