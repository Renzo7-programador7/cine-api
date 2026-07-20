package com.cine.api.service;

import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.service.exception.BusinessValidationException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;

    FuncionService(
            FuncionRepository funcionRepository,
            PeliculaRepository peliculaRepository) {
        this.funcionRepository = funcionRepository;
        this.peliculaRepository = peliculaRepository;
    }

    public List<Funcion> listarTodas() {
        return funcionRepository.findAll();
    }

    public Optional<Funcion> obtenerPorId(Long id) {
        Objects.requireNonNull(id, "El id de la funcion no puede ser nulo");
        return funcionRepository.findById(id);
    }

    @Transactional
    public Funcion guardar(Funcion funcion) {
        validarDatos(funcion);

        Long peliculaId = funcion.getPelicula().getId();
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pelicula no encontrada con id: " + peliculaId));

        LocalDateTime inicio = LocalDateTime.of(funcion.getFecha(), funcion.getHora());
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new BusinessValidationException(
                    "La fecha y hora de la funcion deben ser posteriores al momento actual");
        }

        if (funcionRepository.existsByPelicula_IdAndFechaAndHora(
                peliculaId,
                funcion.getFecha(),
                funcion.getHora())) {
            throw new BusinessValidationException(
                    "La pelicula ya tiene una funcion programada en la fecha y hora indicadas");
        }

        funcion.setPelicula(pelicula);
        return funcionRepository.save(funcion);
    }

    public void eliminar(Long id) {
        Objects.requireNonNull(id, "El id de la funcion no puede ser nulo");
        if (!funcionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Función no encontrada con id: " + id);
        }
        funcionRepository.deleteById(id);
    }

    private void validarDatos(Funcion funcion) {
        if (funcion == null) {
            throw new BusinessValidationException("Los datos de la funcion son obligatorios");
        }
        if (funcion.getFecha() == null || funcion.getHora() == null) {
            throw new BusinessValidationException("La fecha y hora de la funcion son obligatorias");
        }
        if (funcion.getPrecio() <= 0 || funcion.getPrecio() > 100) {
            throw new BusinessValidationException("El precio debe ser mayor a 0 y menor o igual a 100");
        }
        if (funcion.getCapacidad() <= 0 || funcion.getCapacidad() > 1000) {
            throw new BusinessValidationException("La capacidad debe estar entre 1 y 1000");
        }
        if (funcion.getPelicula() == null || funcion.getPelicula().getId() == null) {
            throw new BusinessValidationException("Debe seleccionar una pelicula existente");
        }
    }
}
