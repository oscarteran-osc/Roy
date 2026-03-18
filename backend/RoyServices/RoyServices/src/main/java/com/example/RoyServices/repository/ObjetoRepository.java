package com.example.RoyServices.repository;

import com.example.RoyServices.dto.ObjetoConZonaProjection;
import com.example.RoyServices.model.Objeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Integer> {

    // =================== MÉTODOS SIN ZONA ===================

    List<Objeto> findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion);

    List<Objeto> findByCategoriaIgnoreCase(String categoria);

    List<Objeto> findTop10ByOrderByIdObjetoDesc();

    // =================== MÉTODOS CON ZONA ===================

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        ORDER BY o.id_objeto DESC
    """, nativeQuery = true)
    List<ObjetoConZonaProjection> findAllConZona();

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        WHERE o.id_objeto = :id
    """, nativeQuery = true)
    ObjetoConZonaProjection findByIdConZona(@Param("id") Integer id);

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        WHERE o.nombre_objeto LIKE CONCAT('%', :texto, '%')
           OR o.descripcion LIKE CONCAT('%', :texto, '%')
        ORDER BY o.id_objeto DESC
    """, nativeQuery = true)
    List<ObjetoConZonaProjection> buscarPorTextoConZona(@Param("texto") String texto);

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        WHERE LOWER(o.categoria) = LOWER(:categoria)
        ORDER BY o.id_objeto DESC
    """, nativeQuery = true)
    List<ObjetoConZonaProjection> buscarPorCategoriaConZona(@Param("categoria") String categoria);

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        ORDER BY o.id_objeto DESC
        LIMIT 10
    """, nativeQuery = true)
    List<ObjetoConZonaProjection> findTop10ConZona();

    @Query(value = """
        SELECT
            o.id_objeto AS idObjeto,
            o.id_us_arrendador AS idUsArrendador,
            o.nombre_objeto AS nombreObjeto,
            o.precio AS precio,
            o.estado AS estado,
            o.categoria AS categoria,
            o.descripcion AS descripcion,
            o.imagen_url AS imagenUrl,
            u.zona AS zona,
            CONCAT(u.nombre_us, ' ', u.apellido_us) AS nomArrendador
        FROM objeto o
        LEFT JOIN usuario u ON o.id_us_arrendador = u.id_usuario
        WHERE o.id_us_arrendador = :arrendadorId
        ORDER BY o.id_objeto DESC
    """, nativeQuery = true)
    List<ObjetoConZonaProjection> findByIdUsArrendador(@Param("arrendadorId") Integer arrendadorId);
}