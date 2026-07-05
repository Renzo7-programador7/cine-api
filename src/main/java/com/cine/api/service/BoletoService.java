package com.cine.api.service;

import com.cine.api.entity.Boleto;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BoletoService {

    private final BoletoRepository boletoRepository;

    BoletoService(BoletoRepository boletoRepository) {
        this.boletoRepository = boletoRepository;
    }

    public List<Boleto> listarTodos() {
        return boletoRepository.findAll();
    }

    public Optional<Boleto> obtenerPorId(Long id) {
        Objects.requireNonNull(id, "El id del boleto no puede ser nulo");
        return boletoRepository.findById(id);
    }

    public Boleto guardar(Boleto boleto) {
        Objects.requireNonNull(boleto, "El boleto no puede estar vacio");
        return boletoRepository.save(boleto);
    }

    public void eliminar(Long id) {
        Objects.requireNonNull(id, "El id del boleto no puede ser nulo");
        if (!boletoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleto no encontrado con id: " + id);
        }
        boletoRepository.deleteById(id);
    }

    public Boleto actualizar(Long id, Boleto nuevo) {
        Objects.requireNonNull(nuevo, "No puedes omitir el boleto");
        Objects.requireNonNull(id, "El id del boleto no puede ser nulo");
        if (!boletoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleto no encontrado con id: " + id);
        }
        nuevo.setId(id);
        return boletoRepository.save(nuevo);
    }
}