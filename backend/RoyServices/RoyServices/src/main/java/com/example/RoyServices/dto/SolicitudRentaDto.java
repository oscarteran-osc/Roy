package com.example.RoyServices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRentaDto {
    private Integer idSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer idObjeto;
    private Integer idUsArrendador;
    private Integer idUsArrendatario;
    private Integer diasRenta;
    private Double monto;

    // Campos adicionales
    private String nombreObjeto;
    private String nombreArrendador;
    private String nombreArrendatario;

    // ✅ AGREGAR ESTOS DOS CAMPOS:
    private String imagenObjeto;
    private Double precioObjeto;
}