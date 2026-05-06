package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.SolicitudRentaDto;
import com.example.RoyServices.model.SolicitudRenta;
import com.example.RoyServices.repository.SolicitudRentaRepository;
import com.example.RoyServices.service.SolicitudRentaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SolicitudRentaServiceImpl implements SolicitudRentaService {

    private final SolicitudRentaRepository solicitudRepository;

    @Override
    public List<SolicitudRenta> getAll() {
        return solicitudRepository.findAll();
    }

    @Override
    public SolicitudRenta getById(Integer id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    @Override
    public SolicitudRenta save(SolicitudRenta solicitud) {
        if (solicitud.getFechaInicio().isAfter(solicitud.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        if (solicitud.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy");
        }
        if (solicitud.getIdUsArrendador().equals(solicitud.getIdUsArrendatario())) {
            throw new IllegalArgumentException("El arrendador y arrendatario no pueden ser la misma persona");
        }
        if (!verificarDisponibilidad(solicitud.getIdObjeto(), solicitud.getFechaInicio(), solicitud.getFechaFin())) {
            throw new IllegalArgumentException("El objeto no está disponible en las fechas seleccionadas");
        }
        solicitud.setEstado("PENDIENTE");
        return solicitudRepository.save(solicitud);
    }

    @Override
    public void delete(Integer id) {
        solicitudRepository.deleteById(id);
    }

    @Override
    public SolicitudRenta update(Integer id, SolicitudRenta solicitud) {
        SolicitudRenta existente = solicitudRepository.findById(id).orElse(null);
        if (existente != null) {
            if (!"APROBADA".equalsIgnoreCase(existente.getEstado())) {
                if (solicitud.getFechaInicio().isAfter(solicitud.getFechaFin())) {
                    throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
                }
                existente.setFechaInicio(solicitud.getFechaInicio());
                existente.setFechaFin(solicitud.getFechaFin());
            }
            existente.setEstado(solicitud.getEstado());
            return solicitudRepository.save(existente);
        }
        return null;
    }

    @Override
    public List<SolicitudRenta> getSolicitudesPorEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }

    @Override
    public List<SolicitudRenta> getSolicitudesPorArrendador(Integer idUsArrendador) {
        return solicitudRepository.findByIdUsArrendador(idUsArrendador);
    }

    @Override
    public List<SolicitudRenta> getSolicitudesPorArrendatario(Integer idUsArrendatario) {
        return solicitudRepository.findByIdUsArrendatario(idUsArrendatario);
    }

    @Override
    public List<SolicitudRenta> getSolicitudesPorObjeto(Integer idObjeto) {
        return solicitudRepository.findByIdObjeto(idObjeto);
    }

    @Override
    public SolicitudRenta aprobarSolicitud(Integer idSolicitud) {
        SolicitudRenta solicitud = solicitudRepository.findById(idSolicitud).orElse(null);
        if (solicitud != null && "PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            solicitud.setEstado("APROBADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede aprobar esta solicitud");
    }

    @Override
    public SolicitudRenta rechazarSolicitud(Integer idSolicitud) {
        SolicitudRenta solicitud = solicitudRepository.findById(idSolicitud).orElse(null);
        if (solicitud != null && "PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            solicitud.setEstado("RECHAZADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede rechazar esta solicitud");
    }

    @Override
    public SolicitudRenta completarSolicitud(Integer idSolicitud) {
        SolicitudRenta solicitud = solicitudRepository.findById(idSolicitud).orElse(null);
        if (solicitud != null && "APROBADA".equalsIgnoreCase(solicitud.getEstado())) {
            solicitud.setEstado("COMPLETADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede completar esta solicitud");
    }

    @Override
    public boolean verificarDisponibilidad(Integer idObjeto, LocalDate fechaInicio, LocalDate fechaFin) {
        List<SolicitudRenta> conflictivas = solicitudRepository.findSolicitudesConflictivas(idObjeto, fechaInicio, fechaFin);
        return conflictivas.isEmpty();
    }

    @Override
    public List<SolicitudRenta> getSolicitudesProximasAVencer(Integer dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(dias);
        return solicitudRepository.findSolicitudesProximasAVencer(hoy, fechaLimite);
    }

    @Override
    public Long contarSolicitudesPendientes(Integer idUsArrendador) {
        return solicitudRepository.countByIdUsArrendadorAndEstado(idUsArrendador, "PENDIENTE");
    }

    @Override
    public List<SolicitudRentaDto> getSolicitudesArrendadorConDetalles(Integer idArrendador) {
        List<Object[]> resultados = solicitudRepository.findSolicitudesArrendadorConDetalles(idArrendador);
        return mapearResultadosADto(resultados);
    }

    @Override
    public List<SolicitudRentaDto> getSolicitudesArrendatarioConDetalles(Integer idArrendatario) {
        List<Object[]> resultados = solicitudRepository.findSolicitudesArrendatarioConDetalles(idArrendatario);
        return mapearResultadosADto(resultados);
    }

    private List<SolicitudRentaDto> mapearResultadosADto(List<Object[]> resultados) {
        List<SolicitudRentaDto> solicitudes = new ArrayList<>();
        for (Object[] row : resultados) {
            try {
                LocalDate fechaInicio = row[5] != null ? ((Date) row[5]).toLocalDate() : null;
                LocalDate fechaFin    = row[6] != null ? ((Date) row[6]).toLocalDate() : null;
                int diasRenta = 0;
                if (fechaInicio != null && fechaFin != null) {
                    diasRenta = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
                }
                SolicitudRentaDto dto = SolicitudRentaDto.builder()
                        .idSolicitud((Integer) row[0])
                        .idUsArrendatario((Integer) row[1])
                        .idUsArrendador((Integer) row[2])
                        .idObjeto((Integer) row[3])
                        .estado((String) row[4])
                        .fechaInicio(fechaInicio)
                        .fechaFin(fechaFin)
                        .monto(row[7] != null ? ((Number) row[7]).doubleValue() : 0.0)
                        .nombreObjeto((String) row[9])
                        .imagenObjeto((String) row[10])
                        .precioObjeto(row[11] != null ? ((Number) row[11]).doubleValue() : 0.0)
                        .nombreArrendatario((String) row[12])
                        .nombreArrendador((String) row[13])
                        .diasRenta(diasRenta)
                        .build();
                solicitudes.add(dto);
            } catch (Exception e) {
                System.err.println("Error al mapear solicitud: " + e.getMessage());
            }
        }
        return solicitudes;
    }
}