package com.example.RoyServices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // 1. Define la cadena de filtros de seguridad.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF (necesario para APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Define las reglas de autorización de peticiones HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir el acceso sin autenticación a /api/auth/** (Registro y Login)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Las demás peticiones requieren autenticación
                        .anyRequest().authenticated()
                )

                // Hacemos la aplicación stateless (sin sesiones)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // 2. Crea el Bean para el cifrador de contraseñas (BCryptPasswordEncoder)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}