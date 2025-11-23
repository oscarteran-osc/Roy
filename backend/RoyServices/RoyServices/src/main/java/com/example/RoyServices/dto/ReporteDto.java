package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReporteDto {
    private Integer idReporte;
    private Integer idUsuarioReportante;
    private Integer idUsuarioReportado;
    private Integer idObjetoReportado;
    private String motivo;
    private String descripcion;
    private String estado;
    private LocalDate fechaReporte;
}

