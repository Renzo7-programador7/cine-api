package com.cine.api.service;

import com.cine.api.dto.ComprarBoletoRequest;
import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Usuario;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.service.exception.BusinessValidationException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BoletoService {

    private final BoletoRepository boletoRepository;
    private final FuncionRepository funcionRepository;
    private final UsuarioService usuarioService;

    BoletoService(
            BoletoRepository boletoRepository,
            FuncionRepository funcionRepository,
            UsuarioService usuarioService) {
        this.boletoRepository = boletoRepository;
        this.funcionRepository = funcionRepository;
        this.usuarioService = usuarioService;
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

    @Transactional
    public Boleto comprar(ComprarBoletoRequest request, String emailComprador) {
        if (request == null) {
            throw new BusinessValidationException("Los datos de la compra son obligatorios");
        }
        if (request.getFuncionId() == null || request.getFuncionId() <= 0) {
            throw new BusinessValidationException("Debe seleccionar una funcion valida");
        }
        if (request.getAsiento() == null || request.getAsiento() <= 0) {
            throw new BusinessValidationException("El asiento debe ser mayor a cero");
        }

        Funcion funcion = funcionRepository.findByIdForUpdate(request.getFuncionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcion no encontrada con id: " + request.getFuncionId()));

        LocalDateTime inicioFuncion = LocalDateTime.of(funcion.getFecha(), funcion.getHora());
        if (!inicioFuncion.isAfter(LocalDateTime.now())) {
            throw new BusinessValidationException("No se pueden comprar boletos para una funcion finalizada");
        }
        if (request.getAsiento() > funcion.getCapacidad()) {
            throw new BusinessValidationException(
                    "El asiento debe estar entre 1 y " + funcion.getCapacidad());
        }
        if (boletoRepository.existsByFuncion_IdAndAsientoAndEstado(
                funcion.getId(), request.getAsiento(), "ACTIVO")) {
            throw new BusinessValidationException("El asiento seleccionado ya esta ocupado");
        }

        Usuario comprador = usuarioService.findByEmail(emailComprador);
        Boleto boleto = new Boleto();
        boleto.setFuncion(funcion);
        boleto.setUsuario(comprador);
        boleto.setAsiento(request.getAsiento());
        boleto.setPrecio(funcion.getPrecio());
        boleto.setEstado("ACTIVO");
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
