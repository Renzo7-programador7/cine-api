package com.cine.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cine.api.security.JwtUtil;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String obtenerTokenAdmin() throws Exception {
        return jwtUtil.generateToken("admin@test.com", "ADMIN");
    }

    @Test
    void listarUsuarios_comoAdmin_retorna200() throws Exception {

        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void listarUsuarios_sinToken_retorna401() throws Exception {

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointsAdministrativos_comoUser_retornan403() throws Exception {
        String token = jwtUtil.generateToken("cliente@test.com", "USER");
        String usuario = """
                {
                  "nombre":"Cliente",
                  "email":"cliente.nuevo@test.com",
                  "password":"123456",
                  "rol":"USER"
                }
                """;

        mockMvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/usuarios").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(usuario))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/usuarios/1").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(usuario))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/usuarios/1").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(usuario))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/usuarios/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerUsuario_inexistente_retorna404() throws Exception {

        String token = obtenerTokenAdmin();

        mockMvc.perform(get("/api/usuarios/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarUsuario_inexistente_retorna404() throws Exception {

        String token = obtenerTokenAdmin();

        mockMvc.perform(delete("/api/usuarios/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
