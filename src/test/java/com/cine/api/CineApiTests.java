package com.cine.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dto.ProgramarFuncionRequest;
import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.entity.Usuario;
import com.cine.api.service.BoletoService;
import com.cine.api.service.FuncionService;
import com.cine.api.service.PeliculaService;
import com.cine.api.service.UsuarioService;
import com.cine.api.service.exception.ResourceNotFoundException;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CineApiTests {

    @Autowired PeliculaService peliculaService;
    @Autowired BoletoService boletoService;
    @Autowired UsuarioService usuarioService;
    @Autowired FuncionService funcionService;

    // ===== PELICULA TESTS =====
    @Test @Order(1)
    void crearPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo("Avatar"); p.setDuracion(162);
        p.setClasificacion("PG-13"); p.setGenero("Ciencia Ficción");
        assertNotNull(peliculaService.guardar(p).getId());
    }

    @Test @Order(2)
    void listarPeliculas() {
        Pelicula p = new Pelicula();
        p.setTitulo("Avatar"); p.setDuracion(162);
        p.setClasificacion("PG-13"); p.setGenero("Ciencia Ficción");
        peliculaService.guardar(p);
        assertFalse(peliculaService.listarTodas().isEmpty());
    }

    @Test @Order(3)
    void obtenerPeliculaPorId() {
        Pelicula p = new Pelicula();
        p.setTitulo("Interstellar"); p.setDuracion(169);
        p.setClasificacion("PG-13"); p.setGenero("Sci-Fi");
        Pelicula guardada = peliculaService.guardar(p);
        assertTrue(peliculaService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(4)
    void actualizarPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo("Original"); p.setDuracion(100);
        p.setClasificacion("G"); p.setGenero("Drama");
        Pelicula guardada = peliculaService.guardar(p);
        guardada.setTitulo("Actualizada");
        assertEquals("Actualizada", peliculaService.actualizar(guardada.getId(), guardada).getTitulo());
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
    void peliculaTituloNoNulo() {
        Pelicula p = new Pelicula();
        p.setTitulo("Test"); p.setDuracion(120);
        p.setClasificacion("G"); p.setGenero("Comedia");
        assertNotNull(peliculaService.guardar(p).getTitulo());
    }

    // ===== USUARIO TESTS =====
    @Test @Order(7)
    void crearUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Juan"); u.setEmail("juan@mail.com");
        u.setPassword("123456"); u.setRol("USER");
        assertNotNull(usuarioService.guardar(u).getId());
    }

    @Test @Order(8)
    void listarUsuarios() {
        Usuario u = new Usuario();
        u.setNombre("Juan"); u.setEmail("juan@mail.com");
        u.setPassword("123456"); u.setRol("USER");
        usuarioService.guardar(u);
        assertFalse(usuarioService.listarTodos().isEmpty());
    }

    @Test @Order(9)
    void obtenerUsuarioPorId() {
        Usuario u = new Usuario();
        u.setNombre("Ana"); u.setEmail("ana@mail.com");
        u.setPassword("123456"); u.setRol("ADMIN");
        Usuario guardado = usuarioService.guardar(u);
        assertTrue(usuarioService.obtenerPorId(guardado.getId()).isPresent());
    }

    @Test @Order(10)
    void emailUsuarioNoNulo() {
        Usuario u = new Usuario();
        u.setNombre("Luis"); u.setEmail("luis@mail.com");
        u.setPassword("123456"); u.setRol("USER");
        assertEquals("luis@mail.com", usuarioService.guardar(u).getEmail());
    }

    @Test @Order(11)
    void rolUsuarioCorrecto() {
        Usuario u = new Usuario();
        u.setNombre("Maria"); u.setEmail("maria@mail.com");
        u.setPassword("123456"); u.setRol("ADMIN");
        assertEquals("ADMIN", usuarioService.guardar(u).getRol());
    }

    @Test @Order(12)
    void eliminarUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Temp"); u.setEmail("temp@mail.com");
        u.setPassword("123456"); u.setRol("USER");
        Usuario guardado = usuarioService.guardar(u);
        usuarioService.eliminar(guardado.getId());
        assertFalse(usuarioService.obtenerPorId(guardado.getId()).isPresent());
    }

    // ===== BOLETO TESTS =====
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

    @Test @Order(18)
    void precioBoletoCorrecto() {
        Boleto b = new Boleto();
        b.setPrecio(25.0); b.setEstado("ACTIVO"); b.setAsiento(3);
        assertEquals(25.0, boletoService.guardar(b).getPrecio());
    }

    // ===== FUNCION TESTS =====
    private ProgramarFuncionRequest nuevaFuncionValida(String titulo, double precio, int capacidad) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo); pelicula.setDuracion(120);
        pelicula.setClasificacion("PG"); pelicula.setGenero("Drama");
        pelicula = peliculaService.guardar(pelicula);

        ProgramarFuncionRequest request = new ProgramarFuncionRequest();
        request.setFecha(java.time.LocalDate.now().plusDays(1));
        request.setHora(java.time.LocalTime.of(20, 0));
        request.setPrecio(precio); request.setCapacidad(capacidad);
        request.setPeliculaId(pelicula.getId());
        return request;
    }

    @Test @Order(19)
    void crearFuncion() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion crear", 12.0, 100);
        assertNotNull(funcionService.programar(request).getId());
    }

    @Test @Order(20)
    void listarFunciones() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion listar", 12.0, 100);
        funcionService.programar(request);
        assertFalse(funcionService.listarTodas().isEmpty());
    }

    @Test @Order(21)
    void obtenerFuncionPorId() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion obtener", 18.0, 80);
        Funcion guardada = funcionService.programar(request);
        assertTrue(funcionService.obtenerPorId(guardada.getId()).isPresent());
    }

    @Test @Order(22)
    void capacidadFuncionCorrecta() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion capacidad", 10.0, 50);
        assertEquals(50, funcionService.programar(request).getCapacidad());
    }

    @Test @Order(23)
    void eliminarFuncion() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion eliminar", 8.0, 30);
        Funcion guardada = funcionService.programar(request);
        Long id = guardada.getId();
        funcionService.eliminar(id);
        assertThrows(ResourceNotFoundException.class, () -> funcionService.eliminar(id));
    }

    @Test @Order(24)
    void precioFuncionCorrecto() {
        ProgramarFuncionRequest request = nuevaFuncionValida("Funcion precio", 22.5, 60);
        assertEquals(22.5, funcionService.programar(request).getPrecio());
    }
}

