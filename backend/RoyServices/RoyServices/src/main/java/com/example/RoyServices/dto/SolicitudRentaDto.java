package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class SolicitudRentaDto {
    private Integer idSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer idObjeto;
    private Integer idUsArrendador;
    private Integer idUsArrendatario;

    // Campos adicionales opcionales
    private String nombreObjeto;
    private String nombreArrendador;
    private String nombreArrendatario;
    private Integer diasRenta; // Calculado
}