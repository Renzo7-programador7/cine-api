package com.cine.api.repository;

import com.cine.api.entity.Boleto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByUsuario_EmailIgnoreCaseOrderByIdDesc(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Boleto b WHERE b.id = :id")
    Optional<Boleto> findByIdForUpdate(@Param("id") Long id);

    boolean existsByFuncion_IdAndAsientoAndEstado(
            Long funcionId,
            Integer asiento,
            String estado);
}
