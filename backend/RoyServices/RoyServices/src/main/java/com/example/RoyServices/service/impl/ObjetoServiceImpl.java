package com.example.RoyServices.service.impl;

import com.example.RoyServices.model.Objeto;
import com.example.RoyServices.repository.ObjetoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ObjetoServiceImpl implements com.example.RoyServices.service.ObjetoService {
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

            // ✅ AGREGA ESTO
            aux.setImagenUrl(objeto.getImagenUrl());

            return objetoRepository.save(aux);
        }
        return null;
    }



    //buscador por texto
    @Override
    public List<Objeto> buscarPorTexto(String texto) {
        return objetoRepository
                .findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(texto, texto);
    }

    //por categoría
    @Override
    public List<Objeto> buscarPorCategoria(String categoria) {
        return objetoRepository.findByCategoriaIgnoreCase(categoria);
    }

    //recomendados (losultimos 10)
    @Override
    public List<Objeto> obtenerRecomendados() {
        return objetoRepository.findTop10ByOrderByIdObjetoDesc();
    }

    //destacado
    @Override
    public Objeto obtenerDestacado() {
        List<Objeto> lista = objetoRepository.findTop10ByOrderByIdObjetoDesc();
        return lista.isEmpty() ? null : lista.get(0);
    }

}