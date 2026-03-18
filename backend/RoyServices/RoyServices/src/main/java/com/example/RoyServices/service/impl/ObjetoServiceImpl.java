package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.ObjetoConZonaProjection;
import com.example.RoyServices.model.Objeto;
import com.example.RoyServices.repository.ObjetoRepository;
import com.example.RoyServices.service.ObjetoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ObjetoServiceImpl implements ObjetoService {

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
        Objeto existente = objetoRepository.findById(id).orElse(null);
        if (existente == null) return null;

        existente.setNombreObjeto(objeto.getNombreObjeto());
        existente.setPrecio(objeto.getPrecio());
        existente.setEstado(objeto.getEstado());
        existente.setCategoria(objeto.getCategoria());
        existente.setDescripcion(objeto.getDescripcion());
        existente.setImagenUrl(objeto.getImagenUrl());

        return objetoRepository.save(existente);
    }

    // =================== SIN ZONA ===================

    @Override
    public List<Objeto> buscarPorTexto(String texto) {
        return objetoRepository.findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(texto, texto);
    }

    @Override
    public List<Objeto> buscarPorCategoria(String categoria) {
        return objetoRepository.findByCategoriaIgnoreCase(categoria);
    }

    @Override
    public List<Objeto> obtenerRecomendados() {
        return objetoRepository.findTop10ByOrderByIdObjetoDesc();
    }

    @Override
    public Objeto obtenerDestacado() {
        List<Objeto> top = objetoRepository.findTop10ByOrderByIdObjetoDesc();
        return top.isEmpty() ? null : top.get(0);
    }

    // =================== CON ZONA ===================

    @Override
    public List<ObjetoConZonaProjection> getAllConZona() {
        return objetoRepository.findAllConZona();
    }

    @Override
    public ObjetoConZonaProjection getByIdConZona(Integer id) {
        return objetoRepository.findByIdConZona(id);
    }

    @Override
    public List<ObjetoConZonaProjection> buscarPorTextoConZona(String texto) {
        return objetoRepository.buscarPorTextoConZona(texto);
    }

    @Override
    public List<ObjetoConZonaProjection> buscarPorCategoriaConZona(String categoria) {
        return objetoRepository.buscarPorCategoriaConZona(categoria);
    }

    @Override
    public List<ObjetoConZonaProjection> obtenerRecomendadosConZona() {
        return objetoRepository.findTop10ConZona();
    }

    @Override
    public ObjetoConZonaProjection obtenerDestacadoConZona(Integer userIdExcluir) {
        try {
            List<ObjetoConZonaProjection> todos = objetoRepository.findAllConZona();

            if (todos == null || todos.isEmpty()) {
                return null;
            }

            // Si no hay userId para excluir, retornar el primero
            if (userIdExcluir == null) {
                return todos.get(0);
            }

            // Filtrar objetos que NO sean del usuario actual
            List<ObjetoConZonaProjection> filtrados = todos.stream()
                    .filter(obj -> obj.getIdUsArrendador() != null &&
                            !obj.getIdUsArrendador().equals(userIdExcluir))
                    .collect(Collectors.toList());

            // Si no quedan objetos después de filtrar, retornar null
            if (filtrados.isEmpty()) {
                return null;
            }

            // Retornar uno aleatorio de los filtrados
            Collections.shuffle(filtrados);
            return filtrados.get(0);
        } catch (Exception e) {
            System.err.println("Error en obtenerDestacadoConZona: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ObjetoConZonaProjection> buscarPorArrendadorConZona(Integer arrendadorId) {
        return objetoRepository.findByIdUsArrendador(arrendadorId);
    }
}