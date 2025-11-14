package com.example.zoe.service.impl;

import com.example.zoe.model.Transaccion;
import com.example.zoe.repository.TransaccionRepository;
import com.example.zoe.service.TransaccionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TransaccionServiceImpl implements TransaccionService {
    private final TransaccionRepository transaccionRepository;

    @Override
    public List<Transaccion> getAll() {
        return transaccionRepository.findAll();
    }

    @Override
    public Transaccion getById(Integer id) {
        return transaccionRepository.findById(id).orElse(null);
    }

    @Override
    public Transaccion save(Transaccion transaccion) {
        return transaccionRepository.save(transaccion);
    }

    @Override
    public void delete(Integer id) {
        transaccionRepository.deleteById(id);
    }

    @Override
    public Transaccion update(Integer id, Transaccion transaccion) {
        Transaccion aux = transaccionRepository.findById(id).orElse(null);
        if (aux != null) {
            aux.setMontoTotal(transaccion.getMontoTotal());
            aux.setFechaPago(transaccion.getFechaPago());
            aux.setMetodoPago(transaccion.getMetodoPago());
            aux.setEstatusPago(transaccion.getEstatusPago());
            aux.setIdSolicitud(transaccion.getIdSolicitud());
            return transaccionRepository.save(aux);
        }
        return null;
    }
}
