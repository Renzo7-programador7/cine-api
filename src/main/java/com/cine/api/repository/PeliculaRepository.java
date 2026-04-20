package com.cine.api.repository;

import com.cine.api.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
	Optional<Pelicula> findByTituloIgnoreCase(String titulo);
}