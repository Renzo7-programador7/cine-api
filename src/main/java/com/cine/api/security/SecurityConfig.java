package com.cine.api.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, ex) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado"))
                .accessDeniedHandler((request, response, ex) ->
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado"))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/peliculas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/funciones").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/peliculas/**").permitAll() // Permitir acceso publico a las peliculas :V
                .requestMatchers(HttpMethod.GET,    "/api/funciones/**").permitAll() // Permitir acceso publico a las funciones :V
                .requestMatchers(HttpMethod.POST,   "/api/boletos").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/boletos/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/boletos/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}