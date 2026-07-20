package com.cine.api.repository;

import com.cine.api.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    boolean existsByPelicula_IdAndFechaAndHora(
            Long peliculaId,
            LocalDate fecha,
            LocalTime hora);
}
