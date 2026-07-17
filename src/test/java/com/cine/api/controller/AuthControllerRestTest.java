package com.cine.api.controller;

import com.cine.api.entity.Usuario;
import com.cine.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void register_valido_confirmaRegistroSinIniciarSesion() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Juan","email":"juan@test.com",
                              "password":"123456","rol":"USER"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.rol").doesNotExist());
    }

    @Test
    void register_intentaCrearAdmin_fuerzaRolUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Intento Admin",
                              "email":"intento.admin@test.com",
                              "password":"123456",
                              "rol":"ADMIN"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.rol").doesNotExist());

        Usuario guardado = usuarioRepository.findByEmailIgnoreCase("intento.admin@test.com").orElseThrow();
        assertThat(guardado.getRol()).isEqualTo("USER");
    }

    @Test
    void login_credencialesCorrectas_retornaToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Maria","email":"maria@test.com",
                              "password":"123456"
                            }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email":"  MARIA@TEST.COM ",
                              "password":"123456"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_passwordIncorrecta_retorna401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Pedro","email":"pedro@test.com",
                              "password":"123456"
                            }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email":"pedro@test.com",
                              "password":"wrongpassword"
                            }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_emailInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"Test","email":"no-es-email",
                              "password":"123456"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_normalizaDatosYCifraPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nombre":"  Ana Torres  ",
                              "email":"  ANA.TORRES@TEST.COM  ",
                              "password":"secreto123"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana.torres@test.com"))
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.token").doesNotExist());

        Usuario guardado = usuarioRepository.findByEmailIgnoreCase("ana.torres@test.com").orElseThrow();
        assertThat(guardado.getNombre()).isEqualTo("Ana Torres");
        assertThat(guardado.getRol()).isEqualTo("USER");
        assertThat(guardado.getPassword()).isNotEqualTo("secreto123");
        assertThat(passwordEncoder.matches("secreto123", guardado.getPassword())).isTrue();
    }

    @Test
    void register_emailDuplicadoIgnorandoMayusculas_retorna409() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"nombre":"Primero","email":"duplicado@test.com","password":"123456"}
                            """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"nombre":"Segundo","email":"DUPLICADO@TEST.COM","password":"123456"}
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.errors.email").isArray());
    }

    @Test
    void register_passwordCorta_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"nombre":"Test","email":"corta@test.com","password":"12345"}
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.password").isArray());
    }
}
