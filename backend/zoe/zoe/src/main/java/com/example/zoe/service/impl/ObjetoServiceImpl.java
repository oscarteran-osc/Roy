package com.example.zoe.service.impl;

import com.example.zoe.model.Objeto;
import com.example.zoe.repository.ObjetoRepository;
import com.example.zoe.service.ObjetoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ObjetoServiceImpl implements com.example.zoe.service.ObjetoService {
    private final ObjetoRepository objetoRepository;

    @Override
    public List<Objeto> getAll() {
        return objetoRepository.findAll();
    }

    @Override
    public Objeto getById(Integer id) {
        return objetoRepository.findById(id).orElse(null);
    }

    @Override
    public Objeto save(Objeto objeto) {
        return objetoRepository.save(objeto);
    }

    @Override
    public void delete(Integer id) {
        objetoRepository.deleteById(id);
    }

    @Override
    public Objeto update(Integer id, Objeto objeto) {
        Objeto aux = objetoRepository.findById(id).orElse(null);
        if (aux != null) {
            aux.setIdUsArrendador(objeto.getIdUsArrendador());
            aux.setNombreObjeto(objeto.getNombreObjeto());
            aux.setPrecio(objeto.getPrecio());
            aux.setEstado(objeto.getEstado());
            aux.setCategoria(objeto.getCategoria());
            aux.setDescripcion(objeto.getDescripcion());
            return objetoRepository.save(aux);
        }
        return null;
    }
}