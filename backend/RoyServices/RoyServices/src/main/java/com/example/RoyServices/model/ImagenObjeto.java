package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "IMAGEN_OBJETO")
public class ImagenObjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer idImagen;

    @Column(name = "id_objeto", nullable = false)
    private Integer idObjeto;

    @Column(name = "url_imagen", nullable = false, length = 255)
    private String urlImagen;

    @Column(name = "es_principal")
    private Boolean esPrincipal;
}

