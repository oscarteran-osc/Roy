package com.example.RoyServices.service;

import com.example.RoyServices.model.CuentaBancaria;
import com.example.RoyServices.model.Usuario; // Mantener si Usuario se usa en otras funciones, si no, se puede quitar.

import java.util.List;

public interface CuentaBancariaService {
    List<CuentaBancaria> getAll( );

    CuentaBancaria getById(Integer id);

    CuentaBancaria save(CuentaBancaria cuentaBancaria);

    void delete(Integer id);

    CuentaBancaria update(Integer id, CuentaBancaria cuentaBancaria);
}