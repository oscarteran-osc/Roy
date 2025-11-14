package com.example.RoyServices.service;

import com.example.RoyServices.model.Resena;

import java.time.LocalDate;
import java.util.List;

public interface ResenaService {

    // CRUD básico
    List<Resena> getAll();
    Resena getById(Integer id);
    Resena save(Resena resena);
    void delete(Integer id);
    Resena update(Integer id, Resena resena);

    // Métodos específicos del negocio
    List<Resena> getResenasPorAutor(Integer idUsAutor);
    List<Resena> getResenasPorReceptor(Integer idUsReceptor);
    List<Resena> getResenasPorCalificacion(Integer calificacion);
    List<Resena> getResenasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    Double getPromedioCalificaciones(Integer idUsuario);
    Long contarResenasRecibidas(Integer idUsuario);

    boolean usuarioYaResenoA(Integer idUsAutor, Integer idUsReceptor);
}
