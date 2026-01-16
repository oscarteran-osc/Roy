package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.ObjetoConZonaProjection;
import com.example.RoyServices.model.Objeto;
import com.example.RoyServices.repository.ObjetoRepository;
import com.example.RoyServices.service.ObjetoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ObjetoServiceImpl implements ObjetoService {

    private final ObjetoRepository objetoRepository;

    // =========================
    // CRUD normal
    // =========================

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
        if (aux == null) return null;

        aux.setIdUsArrendador(objeto.getIdUsArrendador());
        aux.setNombreObjeto(objeto.getNombreObjeto());
        aux.setPrecio(objeto.getPrecio());
        aux.setEstado(objeto.getEstado());
        aux.setCategoria(objeto.getCategoria());
        aux.setDescripcion(objeto.getDescripcion());
        aux.setImagenUrl(objeto.getImagenUrl()); // ✅

        return objetoRepository.save(aux);
    }

    // =========================
    // HOME (sin zona) - legacy
    // =========================

    @Override
    public List<Objeto> buscarPorTexto(String texto) {
        return objetoRepository
                .findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(texto, texto);
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
        List<Objeto> lista = objetoRepository.findTop10ByOrderByIdObjetoDesc();
        return lista.isEmpty() ? null : lista.get(0);
    }

    // =========================
    // ✅ HOME (con zona)
    // =========================

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
    public ObjetoConZonaProjection obtenerDestacadoConZona() {
        List<ObjetoConZonaProjection> lista = objetoRepository.findTop10ConZona();
        return lista.isEmpty() ? null : lista.get(0);
    }
}
