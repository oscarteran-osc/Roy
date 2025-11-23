package com.example.RoyServices.repository;
import com.example.RoyServices.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    // 🟦 Mensajes asociados a una solicitud (usando idSolicitud del modelo)
    List<Mensaje> findByIdSolicitud(Integer idSolicitud);

    // 🟪 Conversación entre dos usuarios (ida y vuelta)
    @Query("SELECT m FROM Mensaje m " +
            "WHERE (m.idRemitente = :idUsuario1 AND m.idDestinatario = :idUsuario2) " +
            "   OR (m.idRemitente = :idUsuario2 AND m.idDestinatario = :idUsuario1) " +
            "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacionEntreUsuarios(Integer idUsuario1, Integer idUsuario2);
}


