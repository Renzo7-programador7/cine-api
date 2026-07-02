package com.cine.api.service;

import com.cine.api.entity.Usuario;
import com.cine.api.repository.UsuarioRepository;
import com.cine.api.service.exception.DuplicateResourceException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId( Long id) {
        Objects.requireNonNull(id, "El ID del usuario no puede ser nulo");
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Objects.requireNonNull(id, "El ID del usuario no puede ser nulo");
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario findByEmail(String email) {
        Objects.requireNonNull(email, "El email no puede ser nulo");
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }
}