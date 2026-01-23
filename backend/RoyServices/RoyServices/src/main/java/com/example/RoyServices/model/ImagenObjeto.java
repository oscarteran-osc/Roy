package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "imagen_objeto")
public class ImagenObjeto {

    @Id
    @Column(name = "id_imagen")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImagen;

    @Column(name = "id_objeto")
    private Integer idObjeto;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "es_principal")
    private Boolean esPrincipal;
}