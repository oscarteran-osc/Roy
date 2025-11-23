package com.example.RoyServices.service;

import com.example.RoyServices.dto.ReporteDto;
import com.example.RoyServices.model.Reporte;

import java.util.List;

public interface ReporteService {

    List<Reporte> getAll();

    Reporte getById(Integer idReporte);

    Reporte crearReporte(ReporteDto dto);

    Reporte cambiarEstado(Integer idReporte, String nuevoEstado);
}
