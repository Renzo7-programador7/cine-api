package com.cine.api.service;

import com.cine.api.entity.Funcion;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionService {

    private final FuncionRepository funcionRepository;

    FuncionService(FuncionRepository funcionRepository) {
        this.funcionRepository = funcionRepository;
    }

    public List<Funcion> listarTodas() {
        return funcionRepository.findAll();
    }

    public Optional<Funcion> obtenerPorId(Long id) {
        return funcionRepository.findById(id);
    }

    public Funcion guardar(Funcion funcion) {
        return funcionRepository.save(funcion);
    }

    public void eliminar(Long id) {
        if (!funcionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Función no encontrada con id: " + id);
        }
        funcionRepository.deleteById(id);
    }
}