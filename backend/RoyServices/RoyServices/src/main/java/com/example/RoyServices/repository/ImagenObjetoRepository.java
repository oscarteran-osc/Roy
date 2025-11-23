package com.example.RoyServices.repository;
import com.example.RoyServices.model.ImagenObjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.stereotype.Repository;


@Repository
public interface ImagenObjetoRepository extends JpaRepository<ImagenObjeto, Integer> {

    // 👇 EL NOMBRE TIENE QUE EMPATAR CON EL CAMPO: idObjeto
    List<ImagenObjeto> findByIdObjeto(Integer idObjeto);
}


