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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PeliculaControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Test
    void crearPelicula_valida_retorna201() throws Exception {
        Pelicula pelicula = buildPelicula("Dune", 155, "PG-13", "Ciencia Ficcion");

        mockMvc.perform(post("/api/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pelicula)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Dune"));
    }

    @Test
    void crearPelicula_tituloVacio_retorna400() throws Exception {
        Pelicula pelicula = buildPelicula("   ", 155, "PG-13", "Ciencia Ficcion");

        mockMvc.perform(post("/api/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pelicula)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("título")));
    }

    @Test
    void crearPelicula_jsonInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/peliculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Dune\",\"duracion\":155,"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void obtenerPelicula_porIdExistente_retorna200() throws Exception {
        Pelicula guardada = peliculaRepository.save(buildPelicula("Interstellar", 169, "PG-13", "Sci-Fi"));

        mockMvc.perform(get("/api/peliculas/{id}", guardada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardada.getId()))
                .andExpect(jsonPath("$.titulo").value("Interstellar"));
    }

    @Test
    void obtenerPelicula_porIdInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/peliculas/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPelicula_idNoNumerico_retorna400() throws Exception {
        mockMvc.perform(get("/api/peliculas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void listarPeliculas_retorna200YLista() throws Exception {
        peliculaRepository.save(buildPelicula("Avatar", 162, "PG-13", "Ciencia Ficcion"));
        peliculaRepository.save(buildPelicula("Titanic", 195, "PG-13", "Drama"));

        mockMvc.perform(get("/api/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void actualizarPelicula_idInexistente_retorna404() throws Exception {
        Pelicula cambios = buildPelicula("Nueva", 120, "PG", "Drama");

        mockMvc.perform(put("/api/peliculas/{id}", 404L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void eliminarPelicula_existente_retorna204() throws Exception {
        Pelicula guardada = peliculaRepository.save(buildPelicula("Borrar", 100, "R", "Terror"));

        mockMvc.perform(delete("/api/peliculas/{id}", guardada.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarPelicula_inexistente_retorna404() throws Exception {
        mockMvc.perform(delete("/api/peliculas/{id}", 123456L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
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
