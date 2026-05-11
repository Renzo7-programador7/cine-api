package com.cine.api.controller;

import com.cine.api.dto.LoginRequest;
import com.cine.api.dto.LoginResponse;
import com.cine.api.entity.Usuario;
import com.cine.api.security.JwtUtil;
import com.cine.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UsuarioService usuarioService;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getRol()));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("USER");
        }
        Usuario saved = usuarioService.guardar(usuario);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRol());
        return ResponseEntity.ok(new LoginResponse(token, saved.getEmail(), saved.getRol()));
    }
}