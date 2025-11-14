package com.example.RoyServices.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UsuarioDto {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private String fecharegistro;
    private String password;
}