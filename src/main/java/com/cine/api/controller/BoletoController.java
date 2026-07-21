package com.cine.api.controller;

import com.cine.api.dto.ComprarBoletoRequest;
import com.cine.api.dto.BoletoResponse;
import com.cine.api.dto.DisponibilidadAsientosResponse;
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
@Tag(name = "Boletos", description = "Compra, consulta y cancelacion de boletos")
@SecurityRequirement(name = "bearerAuth")
public class BoletoController {

    private final BoletoService boletoService;

    BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los boletos", description = "Requiere rol ADMIN.")
    public List<BoletoResponse> listar() {
        return boletoService.listarTodos().stream()
                .map(BoletoResponse::from)
                .toList();
    }

    @GetMapping("/mios")
    @Operation(summary = "Listar mis boletos", description = "Devuelve exclusivamente los boletos del usuario autenticado.")
    public List<BoletoResponse> listarMios(Authentication authentication) {
        return boletoService.listarDelUsuario(authentication.getName()).stream()
                .map(BoletoResponse::from)
                .toList();
    }

    @GetMapping("/funciones/{funcionId}/asientos")
    @Operation(summary = "Consultar asientos", description = "Devuelve la capacidad y los asientos activos ocupados de una funcion.")
    public DisponibilidadAsientosResponse consultarAsientos(@PathVariable Long funcionId) {
        return boletoService.consultarDisponibilidad(funcionId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener boleto por ID", description = "Requiere rol ADMIN.")
    public ResponseEntity<BoletoResponse> obtener(@PathVariable Long id) {
        return boletoService.obtenerPorId(id)
                .map(BoletoResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comprar un boleto", description = "Obtiene el comprador del JWT y calcula precio y estado desde los datos del servidor.")
    public BoletoResponse crear(
            @Valid @RequestBody ComprarBoletoRequest request,
            Authentication authentication) {
        return BoletoResponse.from(
                boletoService.comprar(request, authentication.getName()));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar boleto", description = "El cliente solo puede cancelar sus propios boletos activos antes de la funcion.")
    public BoletoResponse cancelar(@PathVariable Long id, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return BoletoResponse.from(
                boletoService.cancelar(id, authentication.getName(), esAdmin));
    }

}
