package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.ReporteDto;
import com.example.RoyServices.model.Reporte;
import com.example.RoyServices.repository.ReporteRepository;
import com.example.RoyServices.service.ReporteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    @Override
    public List<Reporte> getAll() {
        return reporteRepository.findAll();
    }

    @Override
    public Reporte getById(Integer idReporte) {
        return reporteRepository.findById(idReporte).orElse(null);
    }

    @Override
    public Reporte crearReporte(ReporteDto dto) {
        Reporte r = new Reporte();
        r.setIdUsuarioReportante(dto.getIdUsuarioReportante());
        r.setIdUsuarioReportado(dto.getIdUsuarioReportado());
        r.setIdObjetoReportado(dto.getIdObjetoReportado());
        r.setMotivo(dto.getMotivo());
        r.setDescripcion(dto.getDescripcion());
        r.setEstado(dto.getEstado() != null ? dto.getEstado() : "pendiente");
        r.setFechaReporte(dto.getFechaReporte() != null ? dto.getFechaReporte() : LocalDate.now());
        return reporteRepository.save(r);
    }

    @Override
    public Reporte cambiarEstado(Integer idReporte, String nuevoEstado) {
        Reporte r = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        r.setEstado(nuevoEstado);
        return reporteRepository.save(r);
    }
}
