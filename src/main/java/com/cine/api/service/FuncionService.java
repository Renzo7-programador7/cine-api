package com.cine.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cine.api.entity.Funcion;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.service.exception.ResourceNotFoundException;

@Service
public class FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    public List<Funcion> listarTodas() {
        return funcionRepository.findAll();
    }

    public Funcion obtenerPorId(Long id) {
        return funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcion no encontrada con id: " + id));
    }

    public Funcion guardar(Funcion funcion) {
        return funcionRepository.save(funcion);
    }

    public void eliminar(Long id) {
        if (!funcionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Funcion no encontrada con id: " + id);
        }
        funcionRepository.deleteById(id);
    }
}