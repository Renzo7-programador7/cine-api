package com.cine.api;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cine.api.entity.Usuario;
import com.cine.api.repository.UsuarioRepository;

@SpringBootTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testBuscarPorEmail() {
        Usuario user = new Usuario();
        user.setNombre("Renzo Prueba");
        user.setEmail("renzo@test.com");
        user.setPassword("123456");
        user.setRol("ROLE_USER");
        
        usuarioRepository.save(user);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("renzo@test.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Renzo Prueba");
        
        System.out.println("✅ TEST PASADO EXITOSAMENTE EN LA CARPETA CORRECTA");
    }
}