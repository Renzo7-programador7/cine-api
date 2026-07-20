package com.cine.api.controller;

import com.cine.api.dto.ComprarBoletoRequest;
import com.cine.api.entity.Boleto;
import com.cine.api.service.BoletoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/boletos")
@Tag(name = "Boletos", description = "Compra de boletos y operaciones administrativas")
@SecurityRequirement(name = "bearerAuth")
public class BoletoController {

    private final BoletoService boletoService;

    BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los boletos", description = "Requiere rol ADMIN.")
    public List<Boleto> listar() {
        return boletoService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener boleto por ID", description = "Requiere rol ADMIN.")
    public ResponseEntity<Boleto> obtener(@PathVariable Long id) {
        return boletoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comprar un boleto", description = "Obtiene el comprador del JWT y calcula precio y estado desde los datos del servidor.")
    public Boleto crear(
            @Valid @RequestBody ComprarBoletoRequest request,
            Authentication authentication) {
        return boletoService.comprar(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar boleto", description = "Requiere un usuario autenticado.")
    public Boleto actualizar(@PathVariable Long id, @RequestBody Boleto boleto) {
        return boletoService.actualizar(id, boleto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar boleto", description = "Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boletoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
