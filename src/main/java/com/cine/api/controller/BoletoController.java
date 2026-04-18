package com.cine.api.controller;

import com.cine.api.entity.Boleto;
import com.cine.api.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @GetMapping
    public List<Boleto> listar() {
        return boletoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boleto> obtener(@PathVariable Long id) {
        return boletoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Boleto crear(@RequestBody Boleto boleto) {
        return boletoService.guardar(boleto);
    }

    @PutMapping("/{id}")
    public Boleto actualizar(@PathVariable Long id, @RequestBody Boleto boleto) {
        return boletoService.actualizar(id, boleto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boletoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}