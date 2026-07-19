package com.cine.api.controller;

import com.cine.api.entity.Pelicula;
import com.cine.api.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@Tag(name = "Películas", description = "Consulta pública y mantenimiento administrativo de películas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @GetMapping
    @Operation(summary = "Listar películas", description = "Endpoint público.")
    public ResponseEntity<List<Pelicula>> listar() {
        return ResponseEntity.ok(peliculaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener película por ID", description = "Endpoint público.")
    public ResponseEntity<Pelicula> obtener(@PathVariable Long id) {
        return peliculaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear película", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Pelicula> crear(@Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.guardar(pelicula));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar película", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Pelicula> actualizar(@PathVariable Long id, @Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.ok(peliculaService.actualizar(id, pelicula));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar película", description = "Requiere rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
