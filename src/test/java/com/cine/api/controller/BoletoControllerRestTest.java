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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dto.ComprarBoletoRequest;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.entity.Usuario;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.repository.UsuarioRepository;
import com.cine.api.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoletoControllerRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private FuncionRepository funcionRepository;
    @Autowired private PeliculaRepository peliculaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    private String obtenerToken() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Admin Boletos");
        usuario.setEmail("boleto@test.com");
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setRol("ADMIN");
        usuarioRepository.save(usuario);
        return jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
    }

    @Test
    void listarBoletos_retorna200() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(get("/api/boletos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void listarBoletos_comoUser_retorna403() throws Exception {
        String token = jwtUtil.generateToken("cliente@test.com", "USER");

        mockMvc.perform(get("/api/boletos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
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
        funcion.setFecha(java.time.LocalDate.now().plusDays(1));
        funcion.setHora(java.time.LocalTime.of(18, 0));
        funcion.setPrecio(15.0); funcion.setCapacidad(100);
        funcion.setPelicula(pelicula);
        funcion = funcionRepository.save(funcion);

        ComprarBoletoRequest request = new ComprarBoletoRequest();
        request.setFuncionId(funcion.getId());
        request.setAsiento(5);

        mockMvc.perform(post("/api/boletos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.precio").value(15.0))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.usuario.email").value("boleto@test.com"));
    }

    @Test
    void crearBoleto_comoUser_asignaCompradorDesdeToken() throws Exception {
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente");
        cliente.setEmail("cliente.boleto@test.com");
        cliente.setPassword(passwordEncoder.encode("123456"));
        cliente.setRol("USER");
        cliente = usuarioRepository.save(cliente);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setNombre("Otro usuario");
        otroUsuario.setEmail("otro.usuario@test.com");
        otroUsuario.setPassword(passwordEncoder.encode("123456"));
        otroUsuario.setRol("USER");
        otroUsuario = usuarioRepository.save(otroUsuario);

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("Compra Cliente");
        pelicula.setDuracion(100);
        pelicula.setClasificacion("PG");
        pelicula.setGenero("Drama");
        pelicula = peliculaRepository.save(pelicula);

        Funcion funcion = new Funcion();
        funcion.setFecha(java.time.LocalDate.now().plusDays(1));
        funcion.setHora(java.time.LocalTime.of(18, 0));
        funcion.setPrecio(15.0);
        funcion.setCapacidad(100);
        funcion.setPelicula(pelicula);
        funcion = funcionRepository.save(funcion);

        String compraManipulada = """
                {
                  "funcionId": %d,
                  "asiento": 5,
                  "precio": 0.01,
                  "estado": "CANCELADO",
                  "usuario": { "id": %d }
                }
                """.formatted(funcion.getId(), otroUsuario.getId());

        String token = jwtUtil.generateToken(cliente.getEmail(), cliente.getRol());
        mockMvc.perform(post("/api/boletos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compraManipulada))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.email").value(cliente.getEmail()))
                .andExpect(jsonPath("$.precio").value(15.0))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void crearBoleto_sinAsiento_retorna400() throws Exception {
        ComprarBoletoRequest request = new ComprarBoletoRequest();
        request.setFuncionId(1L);

        mockMvc.perform(post("/api/boletos")
                        .header("Authorization", "Bearer " + obtenerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.asiento").exists());
    }

    @Test
    void eliminarBoleto_inexistente_retorna404() throws Exception {
        String token = obtenerToken();
        mockMvc.perform(delete("/api/boletos/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
