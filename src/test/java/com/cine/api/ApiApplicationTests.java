package com.cine.api;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.entity.Usuario;
import com.cine.api.service.BoletoService;
import com.cine.api.service.FuncionService;
import com.cine.api.service.PeliculaService;
import com.cine.api.service.UsuarioService;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiApplicationTests {

    @Autowired PeliculaService peliculaService;
    @Autowired BoletoService boletoService;
    @Autowired UsuarioService usuarioService;
    @Autowired FuncionService funcionService;

    // ===== PELICULA =====
    @Test @Order(1)
    void crearPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo("Avatar"); p.setDuracion(162);
        p.setClasificacion("PG-13"); p.setGenero("Acción");
        assertNotNull(peliculaService.guardar(p).getId());
    }

    @Test @Order(2)
    void listarPeliculas() {
        Pelicula p = new Pelicula();
        p.setTitulo("Avatar"); p.setDuracion(162);
        p.setClasificacion("PG-13"); p.setGenero("Acción");
        peliculaService.guardar(p);
        assertFalse(peliculaService.listarTodas().isEmpty());
    }

    @Test @Order(3)
    void obtenerPeliculaPorId() {
        Pelicula p = new Pelicula();
        p.setTitulo("Coco"); p.setDuracion(105);
        p.setClasificacion("G"); p.setGenero("Animación");
        Pelicula guardada = peliculaService.guardar(p);
        assertTrue(peliculaService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(4)
    void actualizarPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo("Original_");
        p.setDuracion(100);
        p.setClasificacion("G");
        p.setGenero("Drama");

        Pelicula guardada = peliculaService.guardar(p);

        assertNotNull(guardada.getId());
        assertEquals("Original_", guardada.getTitulo());

        guardada.setTitulo("Actualizada");

        Pelicula actualizada = peliculaService.actualizar(guardada.getId(), guardada);

        assertEquals("Actualizada", actualizada.getTitulo());
        assertEquals(100, actualizada.getDuracion());
        assertEquals("G", actualizada.getClasificacion());
        assertEquals("Drama", actualizada.getGenero());

    }

    @Test @Order(5)
    void eliminarPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo("Borrar"); p.setDuracion(90);
        p.setClasificacion("R"); p.setGenero("Terror");
        Pelicula guardada = peliculaService.guardar(p);
        peliculaService.eliminar(guardada.getId());
        assertFalse(peliculaService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(6)
    void tituloNulo() {
        Pelicula p = new Pelicula();
        p.setTitulo(null);
        p.setDuracion(120);
        p.setClasificacion("G");
        p.setGenero("Comedia");

        assertThrows(Exception.class, () -> {
            peliculaService.guardar(p);
        });
    }

    // ===== USUARIO =====
    @Test @Order(7)
    void crearUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Juan"); u.setEmail("juan@mail.com");
        u.setPassword("1234"); u.setRol("USER");
        assertNotNull(usuarioService.guardar(u).getId());
    }

    @Test @Order(8)
    void listarUsuarios() {
        Usuario u = new Usuario();
        u.setNombre("Juan"); u.setEmail("juan@mail.com");
        u.setPassword("1234"); u.setRol("USER");
        usuarioService.guardar(u);
        assertFalse(usuarioService.listarTodos().isEmpty());
    }

    @Test @Order(9)
    void obtenerUsuarioPorId() {
        Usuario u = new Usuario();
        u.setNombre("Ana"); u.setEmail("ana@mail.com");
        u.setPassword("abc"); u.setRol("ADMIN");
        Usuario guardado = usuarioService.guardar(u);
        assertTrue(usuarioService.obtenerPorId(guardado.getId()).isPresent());
    }

    @Test @Order(10)
    void emailUsuarioCorrecto() {
        Usuario u = new Usuario();
        u.setNombre("Luis"); u.setEmail("luis@mail.com");
        u.setPassword("pass"); u.setRol("USER");
        assertEquals("luis@mail.com", usuarioService.guardar(u).getEmail());
    }

    @Test @Order(11)
    void rolUsuarioCorrecto() {
        Usuario u = new Usuario();
        u.setNombre("Maria"); u.setEmail("maria@mail.com");
        u.setPassword("pass"); u.setRol("ADMIN");
        assertEquals("ADMIN", usuarioService.guardar(u).getRol());
    }

    @Test @Order(12)
    void eliminarUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Temp"); u.setEmail("temp@mail.com");
        u.setPassword("x"); u.setRol("USER");
        Usuario guardado = usuarioService.guardar(u);
        usuarioService.eliminar(guardado.getId());
        assertFalse(usuarioService.obtenerPorId(guardado.getId()).isPresent());
    }

    // ===== BOLETO =====
    @Test @Order(13)
    void crearBoleto() {
        Boleto b = new Boleto();
        b.setPrecio(15.50); b.setEstado("ACTIVO"); b.setAsiento(5);
        assertNotNull(boletoService.guardar(b).getId());
    }

    @Test @Order(14)
    void listarBoletos() {
        Boleto b = new Boleto();
        b.setPrecio(15.50); b.setEstado("ACTIVO"); b.setAsiento(5);
        boletoService.guardar(b);
        assertFalse(boletoService.listarTodos().isEmpty());
    }

    @Test @Order(15)
    void obtenerBoletoPorId() {
        Boleto b = new Boleto();
        b.setPrecio(20.0); b.setEstado("ACTIVO"); b.setAsiento(10);
        Boleto guardado = boletoService.guardar(b);
        assertTrue(boletoService.obtenerPorId(guardado.getId()).isPresent());
    }

    @Test @Order(16)
    void actualizarBoleto() {
        Boleto b = new Boleto();
        b.setPrecio(10.0); b.setEstado("PENDIENTE"); b.setAsiento(1);
        Boleto guardado = boletoService.guardar(b);
        guardado.setEstado("USADO");
        assertEquals("USADO", boletoService.actualizar(guardado.getId(), guardado).getEstado());
    }

    @Test @Order(17)
    void eliminarBoleto() {
        Boleto b = new Boleto();
        b.setPrecio(5.0); b.setEstado("CANCELADO"); b.setAsiento(99);
        Boleto guardado = boletoService.guardar(b);
        boletoService.eliminar(guardado.getId());
        assertFalse(boletoService.obtenerPorId(guardado.getId()).isPresent());
    }

    @Test @Order(18)
    void precioBoletoCorrecto() {
        Boleto b = new Boleto();
        b.setPrecio(25.0); b.setEstado("ACTIVO"); b.setAsiento(3);
        assertEquals(25.0, boletoService.guardar(b).getPrecio());
    }

    // ===== FUNCION =====
    @Test @Order(19)
    void crearFuncion() {
        Funcion f = new Funcion();
        f.setPrecio(12.0); f.setCapacidad(100);
        assertNotNull(funcionService.guardar(f).getId());
    }

    @Test @Order(20)
    void listarFunciones() {
        Funcion f = new Funcion();
        f.setPrecio(12.0); f.setCapacidad(100);
        funcionService.guardar(f);
        assertFalse(funcionService.listarTodas().isEmpty());
    }

    @Test @Order(21)
    void obtenerFuncionPorId() {
        Funcion f = new Funcion();
        f.setPrecio(18.0); f.setCapacidad(80);
        Funcion guardada = funcionService.guardar(f);
        assertTrue(funcionService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(22)
    void capacidadFuncionCorrecta() {
        Funcion f = new Funcion();
        f.setPrecio(10.0); f.setCapacidad(50);
        assertEquals(50, funcionService.guardar(f).getCapacidad());
    }

    @Test @Order(23)
    void eliminarFuncion() {
        Funcion f = new Funcion();
        f.setPrecio(8.0); f.setCapacidad(30);
        Funcion guardada = funcionService.guardar(f);
        funcionService.eliminar(guardada.getId());
        assertFalse(funcionService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(24)
    void precioFuncionCorrecto() {
        Funcion f = new Funcion();
        f.setPrecio(22.5); f.setCapacidad(60);
        assertEquals(22.5, funcionService.guardar(f).getPrecio());
    }
}