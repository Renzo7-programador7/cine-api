package com.cine.api.service;

import com.cine.api.entity.Pelicula;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.service.exception.BusinessValidationException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    public List<Pelicula> listarTodas() {
        return peliculaRepository.findAll();
    }

    public Optional<Pelicula> obtenerPorId(Long id) {
        return peliculaRepository.findById(id);
    }

    public Pelicula guardar(Pelicula pelicula) {
        validatePelicula(pelicula);
        peliculaRepository.findByTituloIgnoreCase(pelicula.getTitulo())
                .ifPresent(existing -> {
                    throw new BusinessValidationException("Ya existe una película con el título indicado");
                });
        return peliculaRepository.save(pelicula);
    }

    public void eliminar(Long id) {
        if (!peliculaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Película no encontrada con id: " + id);
        }
        peliculaRepository.deleteById(id);
    }

    public Pelicula actualizar(Long id, Pelicula nueva) {
        Pelicula existente = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));

        validatePelicula(nueva);

        peliculaRepository.findByTituloIgnoreCase(nueva.getTitulo())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new BusinessValidationException("Ya existe una película con el título indicado");
                    }
                });

        existente.setTitulo(nueva.getTitulo().trim());
        existente.setDuracion(nueva.getDuracion());
        existente.setClasificacion(nueva.getClasificacion().trim());
        existente.setGenero(nueva.getGenero().trim());

        return peliculaRepository.save(existente);
    }

    private void validatePelicula(Pelicula pelicula) {
        if (pelicula == null) {
            throw new BusinessValidationException("La película es obligatoria");
        }
        if (isBlank(pelicula.getTitulo())) {
            throw new BusinessValidationException("El título es obligatorio");
        }
        if (pelicula.getDuracion() == null || pelicula.getDuracion() <= 0) {
            throw new BusinessValidationException("La duración debe ser mayor a 0");
        }
        if (isBlank(pelicula.getClasificacion())) {
            throw new BusinessValidationException("La clasificación es obligatoria");
        }
        if (isBlank(pelicula.getGenero())) {
            throw new BusinessValidationException("El género es obligatorio");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}