package com.cine.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.entity.Usuario;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoletoControllerRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BoletoRepository boletoRepository;
    @Autowired private FuncionRepository funcionRepository;
    @Autowired private PeliculaRepository peliculaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String obtenerToken() throws Exception {
        // Registrar y obtener token
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Test","email":"boleto@test.com",
                              "password":"123456","rol":"ADMIN"
                            }
                        """))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void listarBoletos_retorna200() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(get("/api/boletos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerBoleto_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(get("/api/boletos/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearBoleto_valido_retorna200() throws Exception {
        String token = obtenerToken();

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("TestPeli"); pelicula.setDuracion(100);
        pelicula.setClasificacion("PG"); pelicula.setGenero("Drama");
        pelicula = peliculaRepository.save(pelicula);

        Funcion funcion = new Funcion();
        funcion.setFecha(java.time.LocalDate.now());
        funcion.setHora(java.time.LocalTime.of(18, 0));
        funcion.setPrecio(15.0); funcion.setCapacidad(100);
        funcion.setPelicula(pelicula);
        funcion = funcionRepository.save(funcion);

        Usuario usuario = usuarioRepository.findByEmail("boleto@test.com").orElseThrow();

        Boleto boleto = new Boleto();
        boleto.setPrecio(15.0); boleto.setEstado("ACTIVO");
        boleto.setAsiento(5); boleto.setFuncion(funcion);
        boleto.setUsuario(usuario);

        mockMvc.perform(post("/api/boletos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boleto)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarBoleto_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(delete("/api/boletos/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}