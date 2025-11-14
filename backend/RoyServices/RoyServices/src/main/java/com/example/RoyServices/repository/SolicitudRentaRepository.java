package com.example.RoyServices.repository;

import com.example.RoyServices.model.SolicitudRenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudRentaRepository extends JpaRepository<SolicitudRenta, Integer> {

    // Buscar solicitudes por estado
    List<SolicitudRenta> findByEstado(String estado);

    // Buscar solicitudes de un arrendador (dueño del objeto)
    List<SolicitudRenta> findByIdUsArrendador(Integer idUsArrendador);

    // Buscar solicitudes de un arrendatario (quien renta)
    List<SolicitudRenta> findByIdUsArrendatario(Integer idUsArrendatario);

    // Buscar solicitudes de un objeto específico
    List<SolicitudRenta> findByIdObjeto(Integer idObjeto);

    // Buscar solicitudes de un arrendador con estado específico
    List<SolicitudRenta> findByIdUsArrendadorAndEstado(Integer idUsArrendador, String estado);

    // Buscar solicitudes de un arrendatario con estado específico
    List<SolicitudRenta> findByIdUsArrendatarioAndEstado(Integer idUsArrendatario, String estado);

    // Buscar solicitudes por rango de fechas de inicio
    List<SolicitudRenta> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar solicitudes activas en un rango de fechas (para verificar disponibilidad)
    @Query("SELECT s FROM SolicitudRenta s WHERE s.idObjeto = :idObjeto " +
            "AND s.estado IN ('APROBADA', 'PENDIENTE') " +
            "AND ((s.fechaInicio BETWEEN :fechaInicio AND :fechaFin) " +
            "OR (s.fechaFin BETWEEN :fechaInicio AND :fechaFin) " +
            "OR (s.fechaInicio <= :fechaInicio AND s.fechaFin >= :fechaFin))")
    List<SolicitudRenta> findSolicitudesConflictivas(
            @Param("idObjeto") Integer idObjeto,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // Contar solicitudes pendientes de un arrendador
    Long countByIdUsArrendadorAndEstado(Integer idUsArrendador, String estado);

    // Contar solicitudes de un arrendatario
    Long countByIdUsArrendatario(Integer idUsArrendatario);

    // Verificar si existe una solicitud activa entre dos usuarios para un objeto
    boolean existsByIdObjetoAndIdUsArrendatarioAndEstadoIn(
            Integer idObjeto,
            Integer idUsArrendatario,
            List<String> estados
    );

    // Buscar solicitudes que vencen pronto
    @Query("SELECT s FROM SolicitudRenta s WHERE s.estado = 'APROBADA' " +
            "AND s.fechaFin BETWEEN :hoy AND :fechaLimite")
    List<SolicitudRenta> findSolicitudesProximasAVencer(
            @Param("hoy") LocalDate hoy,
            @Param("fechaLimite") LocalDate fechaLimite
    );
}
