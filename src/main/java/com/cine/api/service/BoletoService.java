package com.cine.api.service;

import com.cine.api.entity.Boleto;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    public List<Boleto> listarTodos() {
        return boletoRepository.findAll();
    }

    public Optional<Boleto> obtenerPorId(Long id) {
        return boletoRepository.findById(id);
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