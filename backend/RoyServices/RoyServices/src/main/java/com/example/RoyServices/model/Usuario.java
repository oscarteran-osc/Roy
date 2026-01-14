package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_us")
    private String nombre;

    @Column(name = "apellido_us")
    private String apellido;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "domicilio")
    private String domicilio;

    @Column(name = "contrasena")  // ✅ Este es el campo que contiene la contraseña cifrada
    private String password;       // ✅ Nombre de la variable en Java

    @Column(name = "fecha_registro")
    private LocalDate fechaDeRegistro;
}

