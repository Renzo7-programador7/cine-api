package com.cine.api.repository;

import com.cine.api.entity.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    boolean existsByFuncion_IdAndAsientoAndEstado(
            Long funcionId,
            Integer asiento,
            String estado);
}
