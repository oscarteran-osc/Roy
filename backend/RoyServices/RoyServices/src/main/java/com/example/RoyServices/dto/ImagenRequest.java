package com.example.RoyServices.dto;

import lombok.Data;

@Data
public class ImagenRequest {
    private Integer idObjeto;
    private String urlImagen;
    private Boolean esPrincipal;
}
