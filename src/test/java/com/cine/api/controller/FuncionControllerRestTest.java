package com.cine.api.controller;

import com.cine.api.dto.ProgramarFuncionRequest;
import com.cine.api.entity.Pelicula;
import com.cine.api.repository.PeliculaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cine.api.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FuncionControllerRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PeliculaRepository peliculaRepository;
    @Autowired private JwtUtil jwtUtil;

    private String obtenerToken() throws Exception {
        return jwtUtil.generateToken("funcion.admin@test.com", "ADMIN");
    }

    @Test
    void listarFunciones_retorna200() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(get("/api/funciones")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerFuncion_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(get("/api/funciones/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearFuncion_valida_retorna200() throws Exception {
        String token = obtenerToken();

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("PeliTest"); pelicula.setDuracion(120);
        pelicula.setClasificacion("PG-13"); pelicula.setGenero("Accion");
        pelicula = peliculaRepository.save(pelicula);

        ProgramarFuncionRequest request = new ProgramarFuncionRequest();
        request.setFecha(java.time.LocalDate.now().plusDays(1));
        request.setHora(java.time.LocalTime.of(20, 0));
        request.setPrecio(20.0); request.setCapacidad(50);
        request.setPeliculaId(pelicula.getId());

        mockMvc.perform(post("/api/funciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void crearFuncion_sinPeliculaId_retorna400() throws Exception {
        ProgramarFuncionRequest request = new ProgramarFuncionRequest();
        request.setFecha(java.time.LocalDate.now().plusDays(1));
        request.setHora(java.time.LocalTime.of(20, 0));
        request.setPrecio(20.0);
        request.setCapacidad(50);

        mockMvc.perform(post("/api/funciones")
                        .header("Authorization", "Bearer " + obtenerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.peliculaId").exists());
    }

    @Test
    void eliminarFuncion_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(delete("/api/funciones/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
