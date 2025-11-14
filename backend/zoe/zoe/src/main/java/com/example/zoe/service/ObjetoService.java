package com.example.zoe.service;

import com.example.zoe.model.Objeto;

import java.util.List;

public interface ObjetoService {
    List<Objeto> getAll();
    Objeto getById(Integer id);
    Objeto save(Objeto objeto);
    void delete(Integer id);
    Objeto update(Integer id, Objeto objeto);
}