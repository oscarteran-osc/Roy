package com.example.RoyServices.service;
import com.example.RoyServices.dto.MensajeRequest;
import com.example.RoyServices.model.Mensaje;

import java.util.List;

public interface MensajeService {

    Mensaje enviarMensaje(Integer idRemitente, MensajeRequest request);

    Mensaje getById(Integer idMensaje);

    List<Mensaje> getAll();

    List<Mensaje> obtenerConversacion(Integer idUsuario1, Integer idUsuario2);

    List<Mensaje> obtenerMensajesPorSolicitud(Integer idSolicitud);

    Mensaje marcarComoLeido(Integer idMensaje);
}


