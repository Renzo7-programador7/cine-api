package com.cine.api.controller;

import com.cine.api.entity.Funcion;
import com.cine.api.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public List<Funcion> listar() {
        return funcionService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        return funcionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Funcion crear(@RequestBody Funcion funcion) {
        return funcionService.guardar(funcion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}