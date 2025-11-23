package com.example.RoyServices.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombreUs;
    private String apellidoUs;
    private String correo;
    private String telefono;
    private String contrasena;
    private String domicilio;
}




