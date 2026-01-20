package com.example.RoyServices.repository;

import com.example.RoyServices.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {

    // Buscar todas las reseñas ESCRITAS por un usuario (como autor)
    List<Resena> findByIdUsAutor(Integer idUsAutor);

    // Buscar todas las reseñas RECIBIDAS por un usuario
    List<Resena> findByIdUsReceptor(Integer idUsReceptor);

        List<Resena> findByIdObjeto(Integer idObjeto);


    // Buscar reseñas por calificación
    List<Resena> findByCalificacion(Integer calificacion);

    // Buscar reseñas con calificación mayor o igual a X
    List<Resena> findByCalificacionGreaterThanEqual(Integer calificacion);

    // Buscar reseñas por rango de fechas
    List<Resena> findByFechaResenaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar reseñas de un receptor con calificación mínima
    List<Resena> findByIdUsReceptorAndCalificacionGreaterThanEqual(
            Integer idUsReceptor,
            Integer calificacion
    );

    // Calcular promedio de calificaciones de un usuario (receptor)
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.idUsReceptor = :idUsuario")
    Double calcularPromedioCalificaciones(@Param("idUsuario") Integer idUsuario);

    // Contar cuántas reseñas ha recibido un usuario
    Long countByIdUsReceptor(Integer idUsReceptor);

    // Verificar si un usuario ya reseñó a otro
    boolean existsByIdUsAutorAndIdUsReceptor(Integer idUsAutor, Integer idUsReceptor);
}
