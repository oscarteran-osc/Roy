package com.example.RoyServices.service;

import com.example.RoyServices.model.Objeto;

import java.util.List;

public interface ObjetoService {
    List<Objeto> getAll();
    Objeto getById(Integer id);
    Objeto save(Objeto objeto);
    void delete(Integer id);
    Objeto update(Integer id, Objeto objeto);

    //Funciones para el home
    List<Objeto> buscarPorTexto(String texto);
    List<Objeto> buscarPorCategoria(String categoria);
    List<Objeto> obtenerRecomendados();
    Objeto obtenerDestacado();
}