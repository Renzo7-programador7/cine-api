package com.cine.api.repository;

import com.cine.api.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Funcion f WHERE f.id = :id")
    Optional<Funcion> findByIdForUpdate(@Param("id") Long id);

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
