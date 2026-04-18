package com.cine.api.service;

import com.cine.api.entity.Funcion;
import com.cine.api.repository.FuncionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

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
        funcionRepository.deleteById(id);
    }
}