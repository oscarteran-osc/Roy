package com.example.RoyServices.controller;

import com.example.RoyServices.dto.AuthResponse;
import com.example.RoyServices.dto.LoginRequest;
import com.example.RoyServices.dto.RegisterRequest;
import com.example.RoyServices.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Registrar la excepción completa para depuración
            logger.error("Error en login:", e);
            // Devolver el error usando el constructor de AuthResponse
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 1. IMPRIMIR EL ERROR COMPLETO en la consola
            logger.error("Error al registrar usuario:", e);

            // 2. DEVOLVER EL MENSAJE al cliente (HTTP 409 Conflict o 400 Bad Request)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthResponse(e.getMessage()));
        }
    }
}