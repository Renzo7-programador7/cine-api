package com.cine.api.repository;

import com.cine.api.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    @Query("""
            SELECT f
            FROM Funcion f
            WHERE f.fecha > :fecha
               OR (f.fecha = :fecha AND f.hora > :hora)
            ORDER BY f.fecha ASC, f.hora ASC
            """)
    List<Funcion> findFuncionesFuturasOrdenadas(
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora);

    boolean existsByPelicula_IdAndFechaAndHora(
            Long peliculaId,
            LocalDate fecha,
            LocalTime hora);
}
