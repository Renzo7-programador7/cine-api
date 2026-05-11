package com.cine.api.controller;

import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.repository.PeliculaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired private FuncionRepository funcionRepository;
    @Autowired private PeliculaRepository peliculaRepository;

    private String obtenerToken() throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Admin","email":"funcion@test.com",
                              "password":"123456","rol":"ADMIN"
                            }
                        """))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
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

        Funcion funcion = new Funcion();
        funcion.setFecha(java.time.LocalDate.now());
        funcion.setHora(java.time.LocalTime.of(20, 0));
        funcion.setPrecio(20.0); funcion.setCapacidad(50);
        funcion.setPelicula(pelicula);

        mockMvc.perform(post("/api/funciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcion)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarFuncion_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(delete("/api/funciones/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}