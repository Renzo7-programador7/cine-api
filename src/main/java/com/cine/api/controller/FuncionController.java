package com.cine.api.controller;

import com.cine.api.dto.ProgramarFuncionRequest;
import com.cine.api.entity.Funcion;
import com.cine.api.service.FuncionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    @Operation(summary = "Listar todas las funciones", description = "Incluye funciones pasadas para la gestión administrativa. Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public List<Funcion> listar() {
        return funcionService.listarTodas();
    }

    @GetMapping("/publicas")
    @Operation(summary = "Listar cartelera pública", description = "Devuelve únicamente funciones futuras, ordenadas por fecha y hora.")
    public List<Funcion> listarPublicas() {
        return funcionService.listarPublicas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener función por ID", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        return funcionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Programar función", description = "Valida película, fecha, hora, precio, capacidad y duplicidad. Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public Funcion crear(@Valid @RequestBody ProgramarFuncionRequest request) {
        return funcionService.programar(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar función", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
