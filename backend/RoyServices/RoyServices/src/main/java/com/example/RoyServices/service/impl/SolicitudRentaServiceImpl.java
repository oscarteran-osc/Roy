package com.example.RoyServices.service.impl;

import com.example.RoyServices.model.SolicitudRenta;
import com.example.RoyServices.repository.SolicitudRentaRepository;
import com.example.RoyServices.service.SolicitudRentaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        // Validaciones
        if (solicitud.getFechaInicio().isAfter(solicitud.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        if (solicitud.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy");
        }

        if (solicitud.getIdUsArrendador().equals(solicitud.getIdUsArrendatario())) {
            throw new IllegalArgumentException("El arrendador y arrendatario no pueden ser la misma persona");
        }

        // Verificar disponibilidad
        if (!verificarDisponibilidad(solicitud.getIdObjeto(),
                solicitud.getFechaInicio(),
                solicitud.getFechaFin())) {
            throw new IllegalArgumentException("El objeto no está disponible en las fechas seleccionadas");
        }

        // Asignar estado inicial si no tiene
        if (solicitud.getEstado() == null || solicitud.getEstado().isEmpty()) {
            solicitud.setEstado("PENDIENTE");
        }

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
            // Solo actualizar fechas si no está aprobada
            if (!"APROBADA".equals(existente.getEstado())) {
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
        if (solicitud != null && "PENDIENTE".equals(solicitud.getEstado())) {
            solicitud.setEstado("APROBADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede aprobar esta solicitud");
    }

    @Override
    public SolicitudRenta rechazarSolicitud(Integer idSolicitud) {
        SolicitudRenta solicitud = solicitudRepository.findById(idSolicitud).orElse(null);
        if (solicitud != null && "PENDIENTE".equals(solicitud.getEstado())) {
            solicitud.setEstado("RECHAZADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede rechazar esta solicitud");
    }

    @Override
    public SolicitudRenta completarSolicitud(Integer idSolicitud) {
        SolicitudRenta solicitud = solicitudRepository.findById(idSolicitud).orElse(null);
        if (solicitud != null && "APROBADA".equals(solicitud.getEstado())) {
            solicitud.setEstado("COMPLETADA");
            return solicitudRepository.save(solicitud);
        }
        throw new IllegalArgumentException("No se puede completar esta solicitud");
    }

    @Override
    public boolean verificarDisponibilidad(Integer idObjeto, LocalDate fechaInicio, LocalDate fechaFin) {
        List<SolicitudRenta> conflictivas = solicitudRepository.findSolicitudesConflictivas(
                idObjeto, fechaInicio, fechaFin
        );
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
}
