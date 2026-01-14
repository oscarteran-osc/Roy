package com.example.RoyServices.service;

import com.example.RoyServices.model.Notificacion;

import java.util.List;

public interface NotificacionService {

    List<Notificacion> getNotificacionesUsuario(Integer idUsuario);

    List<Notificacion> getNoLeidas(Integer idUsuario);

    Notificacion crearNotificacion(Integer idUsuario, String tipo, String mensaje);

    Notificacion marcarComoLeida(Integer idNotificacion);
}

