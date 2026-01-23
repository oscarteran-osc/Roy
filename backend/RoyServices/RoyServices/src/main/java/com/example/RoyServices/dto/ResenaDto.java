package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class ResenaDto {
    private Integer idResena;

    private Integer idObjeto;
    private Integer idUsAutor;
    private Integer idUsReceptor;
    private Integer calificacion;
    private String comentario;
    private LocalDate fechaResena;

    private String nombreAutor;
    private String nombreReceptor;
}

