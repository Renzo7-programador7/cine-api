package com.cine.api.service;

import com.cine.api.dto.ProgramarFuncionRequest;
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
    public Funcion programar(ProgramarFuncionRequest request) {
        validarDatos(request);

        Long peliculaId = request.getPeliculaId();
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pelicula no encontrada con id: " + peliculaId));

        LocalDateTime inicio = LocalDateTime.of(request.getFecha(), request.getHora());
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new BusinessValidationException(
                    "La fecha y hora de la funcion deben ser posteriores al momento actual");
        }

        if (funcionRepository.existsByPelicula_IdAndFechaAndHora(
                peliculaId,
                request.getFecha(),
                request.getHora())) {
            throw new BusinessValidationException(
                    "La pelicula ya tiene una funcion programada en la fecha y hora indicadas");
        }

        Funcion funcion = new Funcion();
        funcion.setFecha(request.getFecha());
        funcion.setHora(request.getHora());
        funcion.setPrecio(request.getPrecio());
        funcion.setCapacidad(request.getCapacidad());
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

    private void validarDatos(ProgramarFuncionRequest request) {
        if (request == null) {
            throw new BusinessValidationException("Los datos de la funcion son obligatorios");
        }
        if (request.getFecha() == null || request.getHora() == null) {
            throw new BusinessValidationException("La fecha y hora de la funcion son obligatorias");
        }
        if (request.getPrecio() == null || request.getPrecio() <= 0 || request.getPrecio() > 100) {
            throw new BusinessValidationException("El precio debe ser mayor a 0 y menor o igual a 100");
        }
        if (request.getCapacidad() == null || request.getCapacidad() <= 0 || request.getCapacidad() > 1000) {
            throw new BusinessValidationException("La capacidad debe estar entre 1 y 1000");
        }
        if (request.getPeliculaId() == null || request.getPeliculaId() <= 0) {
            throw new BusinessValidationException("Debe seleccionar una pelicula existente");
        }
    }
}
