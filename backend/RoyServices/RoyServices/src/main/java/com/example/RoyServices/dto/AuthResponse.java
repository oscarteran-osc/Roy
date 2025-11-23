package com.example.RoyServices.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String token;
}

