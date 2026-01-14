package com.example.RoyServices.service;

import com.example.RoyServices.dto.ObjetoConZonaProjection;
import com.example.RoyServices.model.Objeto;

import java.util.List;

public interface ObjetoService {
    List<Objeto> getAll();
    Objeto getById(Integer id);
    Objeto save(Objeto objeto);
    void delete(Integer id);
    Objeto update(Integer id, Objeto objeto);

    // ✅ Funciones home (SIN zona)
    List<Objeto> buscarPorTexto(String texto);
    List<Objeto> buscarPorCategoria(String categoria);
    List<Objeto> obtenerRecomendados();
    Objeto obtenerDestacado();

    // ✅ Funciones home (CON zona)
    List<ObjetoConZonaProjection> getAllConZona();
    ObjetoConZonaProjection getByIdConZona(Integer id);
    List<ObjetoConZonaProjection> buscarPorTextoConZona(String texto);
    List<ObjetoConZonaProjection> buscarPorCategoriaConZona(String categoria);
    List<ObjetoConZonaProjection> obtenerRecomendadosConZona();
    ObjetoConZonaProjection obtenerDestacadoConZona();
}
