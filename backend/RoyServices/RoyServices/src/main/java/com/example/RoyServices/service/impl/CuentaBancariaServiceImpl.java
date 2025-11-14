package com.example.RoyServices.service.impl;

import com.example.RoyServices.model.CuentaBancaria;
import com.example.RoyServices.repository.CuentaBancariaRepository;
import com.example.RoyServices.service.CuentaBancariaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {
    private final CuentaBancariaRepository cuentaBancariaRepository;

    @Override
    public List<CuentaBancaria> getAll() {
        return cuentaBancariaRepository.findAll();
    }

    @Override
    public CuentaBancaria getById(Integer idCuentaBancaria) {
        return cuentaBancariaRepository.findById(idCuentaBancaria).orElse(null);
    }

    @Override
    public CuentaBancaria save(CuentaBancaria cuentaBancaria) {
        return cuentaBancariaRepository.save(cuentaBancaria);
    }

    @Override
    public void delete(Integer idCuentaBancaria) {
        cuentaBancariaRepository.deleteById(idCuentaBancaria);
    }

    @Override
    public CuentaBancaria update(Integer idCuentaBancaria, CuentaBancaria cuentaBancaria) {
        CuentaBancaria aux = cuentaBancariaRepository.findById(idCuentaBancaria)
                .orElseThrow(() -> new RuntimeException("Cuenta Bancaria no encontrada"));

        if (cuentaBancaria.getNombre() != null) {
            aux.setNombre(cuentaBancaria.getNombre());
        }
        if (cuentaBancaria.getApellido() != null) { // Este es num_tarjeta
            aux.setApellido(cuentaBancaria.getApellido());
        }
        if (cuentaBancaria.getIdUsuario() != null) {
            aux.setIdUsuario(cuentaBancaria.getIdUsuario());
        }

        return cuentaBancariaRepository.save(aux);
    }
}