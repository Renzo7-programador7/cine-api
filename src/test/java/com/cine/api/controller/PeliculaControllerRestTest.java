package com.cine.api.controller;

import com.cine.api.entity.Pelicula;
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
class PeliculaControllerRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PeliculaRepository peliculaRepository;

    private String obtenerTokenAdmin() throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Admin","email":"pelicula@test.com",
                              "password":"123456","rol":"ADMIN"
                            }
                        """))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void crearPelicula_valida_retorna201() throws Exception {
        String token = obtenerTokenAdmin();
        Pelicula pelicula = buildPelicula("Dune", 155, "PG-13", "Ciencia Ficcion");
        mockMvc.perform(post("/api/peliculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pelicula)))
                .andExpect(status().isCreated());
    }

    @Test
    void crearPelicula_tituloVacio_retorna400() throws Exception {
        String token = obtenerTokenAdmin();
        Pelicula pelicula = buildPelicula("   ", 155, "PG-13", "Ciencia Ficcion");
        mockMvc.perform(post("/api/peliculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pelicula)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearPelicula_jsonInvalido_retorna400() throws Exception {
        String token = obtenerTokenAdmin();
        mockMvc.perform(post("/api/peliculas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Dune\",\"duracion\":155,"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPelicula_porIdExistente_retorna200() throws Exception {
        String token = obtenerTokenAdmin();
        Pelicula guardada = peliculaRepository.save(buildPelicula("Interstellar", 169, "PG-13", "Sci-Fi"));
        mockMvc.perform(get("/api/peliculas/{id}", guardada.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPelicula_porIdInexistente_retorna404() throws Exception {
        String token = obtenerTokenAdmin();
        mockMvc.perform(get("/api/peliculas/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPelicula_idNoNumerico_retorna400() throws Exception {
        String token = obtenerTokenAdmin();
        mockMvc.perform(get("/api/peliculas/abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarPeliculas_retorna200YLista() throws Exception {
        String token = obtenerTokenAdmin();
        mockMvc.perform(get("/api/peliculas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarPelicula_idInexistente_retorna404() throws Exception {
        String token = obtenerTokenAdmin();
        Pelicula cambios = buildPelicula("Nueva", 120, "PG", "Drama");
        mockMvc.perform(put("/api/peliculas/404")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarPelicula_existente_retorna204() throws Exception {
        String token = obtenerTokenAdmin();
        Pelicula guardada = peliculaRepository.save(buildPelicula("Borrar", 100, "R", "Terror"));
        mockMvc.perform(delete("/api/peliculas/{id}", guardada.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarPelicula_inexistente_retorna404() throws Exception {
        String token = obtenerTokenAdmin();
        mockMvc.perform(delete("/api/peliculas/123456")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private Pelicula buildPelicula(String titulo, Integer duracion, String clasificacion, String genero) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo);
        pelicula.setDuracion(duracion);
        pelicula.setClasificacion(clasificacion);
        pelicula.setGenero(genero);
        return pelicula;
    }
}