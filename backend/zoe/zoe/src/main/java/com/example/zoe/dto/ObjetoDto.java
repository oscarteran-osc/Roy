package com.example.zoe.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ObjetoDto {
    private Integer idObjeto;
    private Integer idUsArrendador;
    private String nombreObjeto;
    private BigDecimal precio;
    private String estado;
    private String categoria;
    private String descripcion;
}
