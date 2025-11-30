package com.example.RoyServices.repository;

import com.example.RoyServices.model.Objeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Integer> {

    //Agregamos los uqerys para el homefuncionsl

    //Buscar por nombre o descripción(buscador)
    List<Objeto> findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion
    );

    //Buscar por categoría exacta
    List<Objeto> findByCategoriaIgnoreCase(String categoria);

    //Obtener los últimos 10 objetos para los rec
    List<Objeto> findTop10ByOrderByIdObjetoDesc();
}