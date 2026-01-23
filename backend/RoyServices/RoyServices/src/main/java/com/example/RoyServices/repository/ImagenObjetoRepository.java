package com.example.RoyServices.repository;

import com.example.RoyServices.model.ImagenObjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenObjetoRepository extends JpaRepository<ImagenObjeto, Integer> {
    List<ImagenObjeto> findByIdObjeto(Integer idObjeto);
}