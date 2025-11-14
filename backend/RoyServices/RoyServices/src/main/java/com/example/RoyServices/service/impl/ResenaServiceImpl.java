package com.example.RoyServices.service.impl;

import com.example.RoyServices.model.Resena;
import com.example.RoyServices.repository.ResenaRepository;
import com.example.RoyServices.service.ResenaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;

    @Override
    public List<Resena> getAll() {
        return resenaRepository.findAll();
    }

    @Override
    public Resena getById(Integer id) {
        return resenaRepository.findById(id).orElse(null);
    }

    @Override
    public Resena save(Resena resena) {
        // Validar que la calificación esté entre 1 y 5
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }

        // Validar que autor y receptor no sean el mismo
        if (resena.getIdUsAutor().equals(resena.getIdUsReceptor())) {
            throw new IllegalArgumentException("Un usuario no puede reseñarse a sí mismo");
        }

        // Si no tiene fecha, asignar la fecha actual
        if (resena.getFechaResena() == null) {
            resena.setFechaResena(LocalDate.now());
        }

        return resenaRepository.save(resena);
    }

    @Override
    public void delete(Integer id) {
        resenaRepository.deleteById(id);
    }

    @Override
    public Resena update(Integer id, Resena resena) {
        Resena existente = resenaRepository.findById(id).orElse(null);
        if (existente != null) {
            // Validar calificación
            if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
                throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
            }

            existente.setCalificacion(resena.getCalificacion());
            existente.setComentario(resena.getComentario());
            existente.setFechaResena(resena.getFechaResena());

            return resenaRepository.save(existente);
        }
        return null;
    }

    @Override
    public List<Resena> getResenasPorAutor(Integer idUsAutor) {
        return resenaRepository.findByIdUsAutor(idUsAutor);
    }

    @Override
    public List<Resena> getResenasPorReceptor(Integer idUsReceptor) {
        return resenaRepository.findByIdUsReceptor(idUsReceptor);
    }

    @Override
    public List<Resena> getResenasPorCalificacion(Integer calificacion) {
        return resenaRepository.findByCalificacion(calificacion);
    }

    @Override
    public List<Resena> getResenasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return resenaRepository.findByFechaResenaBetween(fechaInicio, fechaFin);
    }

    @Override
    public Double getPromedioCalificaciones(Integer idUsuario) {
        Double promedio = resenaRepository.calcularPromedioCalificaciones(idUsuario);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    public Long contarResenasRecibidas(Integer idUsuario) {
        return resenaRepository.countByIdUsReceptor(idUsuario);
    }

    @Override
    public boolean usuarioYaResenoA(Integer idUsAutor, Integer idUsReceptor) {
        return resenaRepository.existsByIdUsAutorAndIdUsReceptor(idUsAutor, idUsReceptor);
    }
}
