package com.cine.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Test
    void contextLoads() {
        // Si el test llega aquí, significa que la base de datos 
        // y la seguridad están bien configuradas.
        System.out.println("✅ Backend verificado correctamente.");
    }
}