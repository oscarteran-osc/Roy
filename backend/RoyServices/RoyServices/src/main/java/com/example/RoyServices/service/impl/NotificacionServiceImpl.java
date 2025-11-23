package com.example.RoyServices.service.impl;

import com.example.RoyServices.model.Notificacion;
import com.example.RoyServices.repository.NotificacionRepository;
import com.example.RoyServices.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> getNotificacionesUsuario(Integer idUsuario) {
        return notificacionRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    @Override
    public List<Notificacion> getNoLeidas(Integer idUsuario) {
        return notificacionRepository.findByIdUsuarioAndLeidaFalseOrderByFechaCreacionDesc(idUsuario);
    }

    @Override
    public Notificacion crearNotificacion(Integer idUsuario, String tipo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setIdUsuario(idUsuario);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        return notificacionRepository.save(n);
    }

    @Override
    public Notificacion marcarComoLeida(Integer idNotificacion) {
        Notificacion n = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        n.setLeida(true);
        return notificacionRepository.save(n);
    }
}

