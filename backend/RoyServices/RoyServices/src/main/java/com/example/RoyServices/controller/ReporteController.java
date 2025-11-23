package com.example.RoyServices.controller;

import com.example.RoyServices.dto.ReporteDto;
import com.example.RoyServices.model.Reporte;
import com.example.RoyServices.service.ReporteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteDto>> getAll() {
        List<Reporte> reportes = reporteService.getAll();
        if (reportes == null || reportes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                reportes.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDto> getById(@PathVariable Integer id) {
        Reporte r = reporteService.getById(id);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(r));
    }

    @PostMapping
    public ResponseEntity<ReporteDto> crear(@RequestBody ReporteDto dto) {
        Reporte r = reporteService.crearReporte(dto);
        return ResponseEntity.ok(toDto(r));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id,
                                           @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falta el campo 'estado'"));
        }
        Reporte r = reporteService.cambiarEstado(id, estado);
        return ResponseEntity.ok(toDto(r));
    }

    private ReporteDto toDto(Reporte r) {
        return ReporteDto.builder()
                .idReporte(r.getIdReporte())
                .idUsuarioReportante(r.getIdUsuarioReportante())
                .idUsuarioReportado(r.getIdUsuarioReportado())
                .idObjetoReportado(r.getIdObjetoReportado())
                .motivo(r.getMotivo())
                .descripcion(r.getDescripcion())
                .estado(r.getEstado())
                .fechaReporte(r.getFechaReporte())
                .build();
    }
}

