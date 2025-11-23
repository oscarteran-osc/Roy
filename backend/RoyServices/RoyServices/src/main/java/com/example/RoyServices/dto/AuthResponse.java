package com.example.RoyServices.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    // CAMPOS DE RESPUESTA EXITOSA
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String token;

    // --- CAMPO Y CONSTRUCTOR PARA ERRORES ---

    // 1. Campo para almacenar el mensaje de error cuando la autenticación falla
    private String errorMessage;

    // 2. Constructor para crear una respuesta que SOLO contenga el mensaje de error
    public AuthResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.idUsuario = null;
        this.nombre = null;
        this.correo = null;
        this.token = null;
    }
}