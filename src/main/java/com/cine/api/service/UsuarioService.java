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

    public Usuario actualizar(Long id, Usuario usuario) {
        Objects.requireNonNull(id, "El ID del usuario no puede ser nulo");
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setRol(usuario.getRol().toUpperCase());
        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario actualizarParcialmente(Long id, Usuario usuario) {
        Objects.requireNonNull(id, "El ID del usuario no puede ser nulo");
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (usuario.getNombre() != null) {
            usuarioExistente.setNombre(usuario.getNombre());
        }
        if (usuario.getEmail() != null) {
            if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new DuplicateResourceException("Ya existe un usuario con el email: " + usuario.getEmail());
            }
            usuarioExistente.setEmail(usuario.getEmail());
        }
        if (usuario.getPassword() != null) {
            usuarioExistente.setPassword(usuario.getPassword());
        }
        if (usuario.getRol() != null) {
            usuarioExistente.setRol(usuario.getRol().toUpperCase());
        }
        return usuarioRepository.save(usuarioExistente);
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