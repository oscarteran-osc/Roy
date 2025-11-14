package com.example.RoyServices.service;

import com.example.RoyServices.model.SolicitudRenta;

import java.time.LocalDate;
import java.util.List;

public interface SolicitudRentaService {

    // CRUD básico
    List<SolicitudRenta> getAll();
    SolicitudRenta getById(Integer id);
    SolicitudRenta save(SolicitudRenta solicitud);
    void delete(Integer id);
    SolicitudRenta update(Integer id, SolicitudRenta solicitud);

    // Métodos específicos del negocio
    List<SolicitudRenta> getSolicitudesPorEstado(String estado);
    List<SolicitudRenta> getSolicitudesPorArrendador(Integer idUsArrendador);
    List<SolicitudRenta> getSolicitudesPorArrendatario(Integer idUsArrendatario);
    List<SolicitudRenta> getSolicitudesPorObjeto(Integer idObjeto);

    SolicitudRenta aprobarSolicitud(Integer idSolicitud);
    SolicitudRenta rechazarSolicitud(Integer idSolicitud);
    SolicitudRenta completarSolicitud(Integer idSolicitud);

    boolean verificarDisponibilidad(Integer idObjeto, LocalDate fechaInicio, LocalDate fechaFin);
    List<SolicitudRenta> getSolicitudesProximasAVencer(Integer dias);

    Long contarSolicitudesPendientes(Integer idUsArrendador);
}