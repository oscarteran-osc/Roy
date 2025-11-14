package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cuentabancaria")


public class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuentaBancaria;

    @Column(name = "banco")
    private String nombre;

    @Column(name = "numero_tarjeta")
    private String apellido;

   @Column(name = "id_usuario")
    private Integer idUsuario;
}
