package com.cine.api.controller;

import com.cine.api.entity.Funcion;
import com.cine.api.service.FuncionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@Tag(name = "Funciones", description = "Consulta pública y mantenimiento administrativo de funciones")
public class FuncionController {

    private final FuncionService funcionService;

    FuncionController(FuncionService funcionService) {
        this.funcionService = funcionService;
    }

    @GetMapping
    @Operation(summary = "Listar funciones", description = "Endpoint público.")
    public List<Funcion> listar() {
        return funcionService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener función por ID", description = "Endpoint público.")
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        return funcionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear función", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public Funcion crear(@RequestBody Funcion funcion) {
        return funcionService.guardar(funcion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar función", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
