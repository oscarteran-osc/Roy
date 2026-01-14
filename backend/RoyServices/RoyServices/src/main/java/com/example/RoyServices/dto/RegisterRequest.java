package com.example.RoyServices.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String nombre;      // Viene del Android
    private String apellido;    // Viene del Android
    private String correo;      // Viene del Android
    private String telefono;    // Viene del Android
    private String domicilio;   // Viene del Android como "direccion"
    private String password;    // Viene del Android
    private String zona;
    private String fotoUrl;
}