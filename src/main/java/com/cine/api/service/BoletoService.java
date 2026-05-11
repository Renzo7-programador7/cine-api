package com.cine.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cine.api.entity.Boleto;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.service.exception.ResourceNotFoundException;

@Service
public class BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    public List<Boleto> listarTodos() {
        return boletoRepository.findAll();
    }

    public Boleto obtenerPorId(Long id) {
        return boletoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto no encontrado con id: " + id));
    }

    public Boleto guardar(Boleto boleto) {
        return boletoRepository.save(boleto);
    }

    public void eliminar(Long id) {
        if (!boletoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleto no encontrado con id: " + id);
        }
        boletoRepository.deleteById(id);
    }

    public Boleto actualizar(Long id, Boleto nuevo) {
        if (!boletoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleto no encontrado con id: " + id);
        }
        nuevo.setId(id);
        return boletoRepository.save(nuevo);
    }
}