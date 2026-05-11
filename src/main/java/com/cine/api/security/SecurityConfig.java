package com.cine.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores si lo necesitas
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitado para APIs REST (JWT se encarga)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // --- ACCESO PÚBLICO (Documentación y Auth) ---
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                // --- ACCESO SOLO ADMIN ---
                .requestMatchers(HttpMethod.POST,   "/api/peliculas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.POST,   "/api/funciones").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET,    "/api/usuarios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.DELETE, "/api/boletos/**").hasRole("ADMIN")

                // --- ACCESO COMPARTIDO (USER y ADMIN) ---
                .requestMatchers(HttpMethod.GET,    "/api/peliculas/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/funciones/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/boletos").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/boletos/**").hasAnyRole("USER", "ADMIN")

                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )
            // Filtro JWT antes del filtro de usuario/password estándar
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}