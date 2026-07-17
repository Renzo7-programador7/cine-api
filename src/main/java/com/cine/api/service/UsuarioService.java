package com.cine.api.service;

import com.cine.api.entity.Usuario;
import com.cine.api.dto.RegisterRequest;
import com.cine.api.repository.UsuarioRepository;
import com.cine.api.service.exception.DuplicateResourceException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new DuplicateResourceException("email", "Ya existe un usuario con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registrarCliente(RegisterRequest request) {
        Objects.requireNonNull(request, "Los datos de registro son obligatorios");

        String email = normalizarEmail(request.getEmail());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("email", "Ya existe un usuario con el email: " + email);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("USER");
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuario) {
        Objects.requireNonNull(id, "El ID del usuario no puede ser nulo");
        Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        if (!usuarioExistente.getEmail().equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new DuplicateResourceException("email", "Ya existe un usuario con el email: " + usuario.getEmail());
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
            if (!usuarioExistente.getEmail().equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
                throw new DuplicateResourceException("email", "Ya existe un usuario con el email: " + usuario.getEmail());
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
        String emailNormalizado = normalizarEmail(email);
        return usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + emailNormalizado));
    }

    public String normalizarEmail(String email) {
        Objects.requireNonNull(email, "El email no puede ser nulo");
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
