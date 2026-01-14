package com.example.RoyServices.repository;

import com.example.RoyServices.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {

    List<Favorito> findByIdUsuario(Integer idUsuario);

    void deleteByIdUsuarioAndIdObjeto(Integer idUsuario, Integer idObjeto);
}
