package com.example.RoyServices.service;

import com.example.RoyServices.model.Objeto;

import java.util.List;

public interface ObjetoService {
    List<Objeto> getAll();
    Objeto getById(Integer id);
    Objeto save(Objeto objeto);
    void delete(Integer id);
    Objeto update(Integer id, Objeto objeto);
}