package com.cine.api.service;

import com.cine.api.dto.LoginRequest;
import com.cine.api.dto.LoginResponse;
import com.cine.api.dto.RegisterRequest;
import com.cine.api.dto.RegisterResponse;
import com.cine.api.entity.Usuario;
import com.cine.api.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    AutenticacionService(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest request) {
        String email = usuarioService.normalizarEmail(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        Usuario usuario = usuarioService.findByEmail(email);
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        return new LoginResponse(
                token,
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getNombre());
    }

    @Transactional
    public RegisterResponse registrarCliente(RegisterRequest request) {
        Usuario usuario = usuarioService.registrarCliente(request);
        return new RegisterResponse(
                "Usuario registrado correctamente",
                usuario.getEmail());
    }
}
