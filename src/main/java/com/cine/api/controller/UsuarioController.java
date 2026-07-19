package com.cine.api.controller;

import com.cine.api.entity.Usuario;
import com.cine.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Administración de usuarios", description = "Operaciones disponibles únicamente para el rol ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Requiere rol ADMIN.")
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Requiere rol ADMIN.")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario desde administración", description = "Requiere rol ADMIN. Permite crear cuentas USER o ADMIN.")
    public Usuario crear(@Valid @RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar completamente un usuario", description = "Requiere rol ADMIN.")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un usuario", description = "Requiere rol ADMIN.")
    public ResponseEntity<Usuario> parcialmenteActualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.actualizarParcialmente(id, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
