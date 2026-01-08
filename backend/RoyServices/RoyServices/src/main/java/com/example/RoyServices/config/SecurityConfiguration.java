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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ❌ CSRF fuera para API REST
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize -> authorize
                        // ✅ Abrimos login/registro sin autenticación
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ Endpoints de objetos sin auth mientras desarrollas
                        .requestMatchers("/api/objeto/**").permitAll()
                        .requestMatchers("/Roy/api/**").permitAll()

                        // ➕ NUEVO: permitir imágenes y uploads
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/imagenes/**").permitAll()

                        // ✅ Por ahora TODO lo demás también lo permitimos
                        //    (más adelante se puede cambiar a .authenticated())
                        .anyRequest().permitAll()
                )

                // API stateless (sin sesiones de servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
