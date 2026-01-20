package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjetoDto {
    private Integer idObjeto;
    private Integer idUsArrendador;
    private String nombreObjeto;
    private Double precio;
    private String estado;
    private String categoria;
    private String descripcion;
    private String imagenUrl;
    private String zona;
}