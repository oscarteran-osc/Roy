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

    // =========================
    // ✅ HOME + ZONA (JOIN)
    // =========================

    @Query(value = """
        SELECT 
            o.id_objeto          AS idObjeto,
            o.id_us_arrendador   AS idUsArrendador,
            o.nombre_objeto      AS nombreObjeto,
            o.precio             AS precio,
            o.estado             AS estado,
            o.categoria          AS categoria,
            o.descripcion        AS descripcion,
            o.imagen_url         AS imagenUrl,
            u.zona               AS zona
        FROM objeto o
        JOIN usuario u ON u.id_usuario = o.id_us_arrendador
        """, nativeQuery = true)
    List<ObjetoConZonaProjection> findAllConZona();


    @Query(value = """
        SELECT 
            o.id_objeto          AS idObjeto,
            o.id_us_arrendador   AS idUsArrendador,
            o.nombre_objeto      AS nombreObjeto,
            o.precio             AS precio,
            o.estado             AS estado,
            o.categoria          AS categoria,
            o.descripcion        AS descripcion,
            o.imagen_url         AS imagenUrl,
            u.zona               AS zona
        FROM objeto o
        JOIN usuario u ON u.id_usuario = o.id_us_arrendador
        WHERE o.id_objeto = :id
        """, nativeQuery = true)
    ObjetoConZonaProjection findByIdConZona(@Param("id") Integer id);


    @Query(value = """
        SELECT 
            o.id_objeto          AS idObjeto,
            o.id_us_arrendador   AS idUsArrendador,
            o.nombre_objeto      AS nombreObjeto,
            o.precio             AS precio,
            o.estado             AS estado,
            o.categoria          AS categoria,
            o.descripcion        AS descripcion,
            o.imagen_url         AS imagenUrl,
            u.zona               AS zona
        FROM objeto o
        JOIN usuario u ON u.id_usuario = o.id_us_arrendador
        WHERE LOWER(o.nombre_objeto) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(o.descripcion)   LIKE LOWER(CONCAT('%', :texto, '%'))
        """, nativeQuery = true)
    List<ObjetoConZonaProjection> buscarPorTextoConZona(@Param("texto") String texto);


    @Query(value = """
        SELECT 
            o.id_objeto          AS idObjeto,
            o.id_us_arrendador   AS idUsArrendador,
            o.nombre_objeto      AS nombreObjeto,
            o.precio             AS precio,
            o.estado             AS estado,
            o.categoria          AS categoria,
            o.descripcion        AS descripcion,
            o.imagen_url         AS imagenUrl,
            u.zona               AS zona
        FROM objeto o
        JOIN usuario u ON u.id_usuario = o.id_us_arrendador
        WHERE LOWER(o.categoria) = LOWER(:categoria)
        """, nativeQuery = true)
    List<ObjetoConZonaProjection> buscarPorCategoriaConZona(@Param("categoria") String categoria);


    @Query(value = """
        SELECT 
            o.id_objeto          AS idObjeto,
            o.id_us_arrendador   AS idUsArrendador,
            o.nombre_objeto      AS nombreObjeto,
            o.precio             AS precio,
            o.estado             AS estado,
            o.categoria          AS categoria,
            o.descripcion        AS descripcion,
            o.imagen_url         AS imagenUrl,
            u.zona               AS zona
        FROM objeto o
        JOIN usuario u ON u.id_usuario = o.id_us_arrendador
        ORDER BY o.id_objeto DESC
        LIMIT 10
        """, nativeQuery = true)
    List<ObjetoConZonaProjection> findTop10ConZona();


    // =========================
    // ✅ LOS MÉTODOS VIEJOS (por si aún los usas)
    // =========================

    List<Objeto> findByNombreObjetoContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion
    );

    List<Objeto> findByCategoriaIgnoreCase(String categoria);

    List<Objeto> findTop10ByOrderByIdObjetoDesc();
}
