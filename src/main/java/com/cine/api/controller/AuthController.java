package com.cine.api.controller;

import com.cine.api.dto.LoginRequest;
import com.cine.api.dto.LoginResponse;
import com.cine.api.entity.Usuario;
import com.cine.api.security.JwtUtil;
import com.cine.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            Usuario usuario = usuarioService.findByEmail(request.getEmail());
            String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
            return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getRol(), usuario.getNombre()));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("USER");
        }
        Usuario saved = usuarioService.guardar(usuario);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRol());
        return ResponseEntity.ok(new LoginResponse(token, saved.getEmail(), saved.getRol(), saved.getNombre()));
    }
}