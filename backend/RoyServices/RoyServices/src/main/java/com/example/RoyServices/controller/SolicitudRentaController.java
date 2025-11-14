package com.example.RoyServices.controller;

import com.example.RoyServices.dto.SolicitudRentaDto;
import com.example.RoyServices.model.SolicitudRenta;
import com.example.RoyServices.service.SolicitudRentaService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes")
@AllArgsConstructor
public class SolicitudRentaController {

    private final SolicitudRentaService solicitudService;

    // ============================================
    // CRUD BÁSICO
    // ============================================

    // GET todas las solicitudes
    @GetMapping
    public ResponseEntity<List<SolicitudRentaDto>> getAll() {
        List<SolicitudRenta> solicitudes = solicitudService.getAll();
        if (solicitudes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // GET solicitud por ID
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRentaDto> getById(@PathVariable Integer id) {
        SolicitudRenta solicitud = solicitudService.getById(id);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(solicitud));
    }

    // POST crear solicitud
    @PostMapping
    public ResponseEntity<?> save(@RequestBody SolicitudRentaDto dto) {
        try {
            SolicitudRenta solicitud = SolicitudRenta.builder()
                    .fechaInicio(dto.getFechaInicio())
                    .fechaFin(dto.getFechaFin())
                    .estado(dto.getEstado())
                    .idObjeto(dto.getIdObjeto())
                    .idUsArrendador(dto.getIdUsArrendador())
                    .idUsArrendatario(dto.getIdUsArrendatario())
                    .build();

            SolicitudRenta guardada = solicitudService.save(solicitud);
            return ResponseEntity.ok(convertirADto(guardada));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT actualizar solicitud
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody SolicitudRentaDto dto) {
        try {
            SolicitudRenta solicitud = SolicitudRenta.builder()
                    .fechaInicio(dto.getFechaInicio())
                    .fechaFin(dto.getFechaFin())
                    .estado(dto.getEstado())
                    .build();

            SolicitudRenta actualizada = solicitudService.update(id, solicitud);

            if (actualizada == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(convertirADto(actualizada));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE eliminar solicitud
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ENDPOINTS ESPECÍFICOS
    // ============================================

    // GET solicitudes por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudRentaDto>> getPorEstado(@PathVariable String estado) {
        List<SolicitudRenta> solicitudes = solicitudService.getSolicitudesPorEstado(estado);
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // GET solicitudes de un arrendador (dueño)
    @GetMapping("/arrendador/{idArrendador}")
    public ResponseEntity<List<SolicitudRentaDto>> getPorArrendador(@PathVariable Integer idArrendador) {
        List<SolicitudRenta> solicitudes = solicitudService.getSolicitudesPorArrendador(idArrendador);
        if (solicitudes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // GET solicitudes de un arrendatario (quien renta)
    @GetMapping("/arrendatario/{idArrendatario}")
    public ResponseEntity<List<SolicitudRentaDto>> getPorArrendatario(@PathVariable Integer idArrendatario) {
        List<SolicitudRenta> solicitudes = solicitudService.getSolicitudesPorArrendatario(idArrendatario);
        if (solicitudes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // GET solicitudes de un objeto
    @GetMapping("/objeto/{idObjeto}")
    public ResponseEntity<List<SolicitudRentaDto>> getPorObjeto(@PathVariable Integer idObjeto) {
        List<SolicitudRenta> solicitudes = solicitudService.getSolicitudesPorObjeto(idObjeto);
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // PUT aprobar solicitud
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Integer id) {
        try {
            SolicitudRenta aprobada = solicitudService.aprobarSolicitud(id);
            return ResponseEntity.ok(convertirADto(aprobada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT rechazar solicitud
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Integer id) {
        try {
            SolicitudRenta rechazada = solicitudService.rechazarSolicitud(id);
            return ResponseEntity.ok(convertirADto(rechazada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT completar solicitud
    @PutMapping("/{id}/completar")
    public ResponseEntity<?> completar(@PathVariable Integer id) {
        try {
            SolicitudRenta completada = solicitudService.completarSolicitud(id);
            return ResponseEntity.ok(convertirADto(completada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET verificar disponibilidad de un objeto
    @GetMapping("/disponibilidad")
    public ResponseEntity<Map<String, Boolean>> verificarDisponibilidad(
            @RequestParam Integer idObjeto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        boolean disponible = solicitudService.verificarDisponibilidad(idObjeto, fechaInicio, fechaFin);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    // GET solicitudes próximas a vencer
    @GetMapping("/proximas-vencer")
    public ResponseEntity<List<SolicitudRentaDto>> getProximasAVencer(
            @RequestParam(defaultValue = "7") Integer dias
    ) {
        List<SolicitudRenta> solicitudes = solicitudService.getSolicitudesProximasAVencer(dias);
        return ResponseEntity.ok(solicitudes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    // GET contar solicitudes pendientes de un arrendador
    @GetMapping("/pendientes/{idArrendador}")
    public ResponseEntity<Map<String, Long>> contarPendientes(@PathVariable Integer idArrendador) {
        Long total = solicitudService.contarSolicitudesPendientes(idArrendador);
        return ResponseEntity.ok(Map.of("totalPendientes", total));
    }

    // ============================================
    // MÉTODO AUXILIAR
    // ============================================

    private SolicitudRentaDto convertirADto(SolicitudRenta solicitud) {
        // Calcular días de renta
        long diasRenta = ChronoUnit.DAYS.between(
                solicitud.getFechaInicio(),
                solicitud.getFechaFin()
        ) + 1; // +1 para incluir ambos días

        return SolicitudRentaDto.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .fechaInicio(solicitud.getFechaInicio())
                .fechaFin(solicitud.getFechaFin())
                .estado(solicitud.getEstado())
                .idObjeto(solicitud.getIdObjeto())
                .idUsArrendador(solicitud.getIdUsArrendador())
                .idUsArrendatario(solicitud.getIdUsArrendatario())
                .diasRenta((int) diasRenta)
                .build();
    }
}
