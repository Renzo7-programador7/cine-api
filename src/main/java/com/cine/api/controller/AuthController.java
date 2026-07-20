package com.cine.api.controller;

import com.cine.api.dto.LoginRequest;
import com.cine.api.dto.LoginResponse;
import com.cine.api.dto.RegisterRequest;
import com.cine.api.dto.RegisterResponse;
import com.cine.api.service.AutenticacionService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y registro público de clientes")
public class AuthController {

    private final AutenticacionService autenticacionService;

    AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica por email y contraseña. Devuelve un JWT y los datos básicos del usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credenciales correctas"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar cliente", description = "Crea una cuenta pública con rol USER. No inicia sesión ni devuelve un JWT; después del registro se debe usar /api/auth/login.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente registrado correctamente, sin iniciar sesión"),
        @ApiResponse(responseCode = "400", description = "Nombre, email o contraseña inválidos"),
        @ApiResponse(responseCode = "409", description = "El email ya se encuentra registrado")
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(autenticacionService.registrarCliente(request));
    }
}
