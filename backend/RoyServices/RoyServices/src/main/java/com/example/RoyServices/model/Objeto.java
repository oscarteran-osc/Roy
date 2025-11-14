package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "objeto")
public class Objeto {
    @Id
    @Column(name = "id_objeto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObjeto;

    @Column(name = "id_us_arrendador")
    private Integer idUsArrendador;

    @Column(name = "nombre_objeto")
    private String nombreObjeto;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "estado")
    private String estado;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}