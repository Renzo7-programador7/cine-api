package com.cine.api.controller;

import com.cine.api.entity.Boleto;
import com.cine.api.service.BoletoService;
import com.cine.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boletos")
@Tag(name = "Boletos", description = "Compra de boletos y operaciones administrativas")
@SecurityRequirement(name = "bearerAuth")
public class BoletoController {

    private final BoletoService boletoService;
    private final UsuarioService usuarioService;

    BoletoController(BoletoService boletoService, UsuarioService usuarioService) {
        this.boletoService = boletoService;
        this.usuarioService = usuarioService;
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
    @Operation(summary = "Comprar o crear un boleto", description = "Requiere USER o ADMIN. Para USER, el propietario se obtiene del JWT y el estado se establece como ACTIVO.")
    public Boleto crear(@RequestBody Boleto boleto, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            boleto.setUsuario(usuarioService.findByEmail(authentication.getName()));
            boleto.setEstado("ACTIVO");
        }

        return boletoService.guardar(boleto);
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
