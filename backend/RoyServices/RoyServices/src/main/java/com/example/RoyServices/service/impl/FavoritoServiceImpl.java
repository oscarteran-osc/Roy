package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.FavoritoRequest;
import com.example.RoyServices.model.Favorito;
import com.example.RoyServices.repository.FavoritoRepository;
import com.example.RoyServices.service.FavoritoService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;

    @Override
    public List<Favorito> getAll() {
        return favoritoRepository.findAll();
    }

    @Override
    public List<Favorito> getFavoritosPorUsuario(Integer idUsuario) {
        return favoritoRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public Favorito agregarFavorito(Integer idUsuario, FavoritoRequest request) {
        Favorito f = new Favorito();
        f.setIdUsuario(idUsuario);
        f.setIdObjeto(request.getIdObjeto());
        f.setFechaAgregado(LocalDate.now());

        try {
            return favoritoRepository.save(f);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El objeto ya está en favoritos", e);
        }
    }

    @Override
    public void eliminarFavorito(Integer idUsuario, Integer idObjeto) {
        favoritoRepository.deleteByIdUsuarioAndIdObjeto(idUsuario, idObjeto);
    }
}

