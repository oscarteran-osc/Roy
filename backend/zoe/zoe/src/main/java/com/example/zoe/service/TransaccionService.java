package com.example.zoe.service;

import com.example.zoe.model.Transaccion;

import java.util.List;

public interface TransaccionService {
    List<Transaccion> getAll();
    Transaccion getById(Integer id);
    Transaccion save(Transaccion transaccion);
    void delete(Integer id);
    Transaccion update(Integer id, Transaccion transaccion);
}
