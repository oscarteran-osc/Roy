package com.example.RoyServices.service;

import com.example.RoyServices.dto.FavoritoRequest;
import com.example.RoyServices.model.Favorito;

import java.util.List;

public interface FavoritoService {

    List<Favorito> getAll();

    List<Favorito> getFavoritosPorUsuario(Integer idUsuario);

    Favorito agregarFavorito(Integer idUsuario, FavoritoRequest request);

    void eliminarFavorito(Integer idUsuario, Integer idObjeto);
}

