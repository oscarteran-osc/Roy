package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.MensajeRequest;
import com.example.RoyServices.model.Mensaje;
import com.example.RoyServices.repository.MensajeRepository;
import com.example.RoyServices.service.MensajeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;

    @Override
    public Mensaje enviarMensaje(Integer idRemitente, MensajeRequest request) {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdRemitente(idRemitente);
        mensaje.setIdDestinatario(request.getIdDestinatario());
        mensaje.setIdSolicitud(request.getIdSolicitud());
        mensaje.setContenido(request.getContenido());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);

        return mensajeRepository.save(mensaje);
    }

    @Override
    public Mensaje getById(Integer idMensaje) {
        return mensajeRepository.findById(idMensaje).orElse(null);
    }

    @Override
    public List<Mensaje> getAll() {
        return mensajeRepository.findAll();
    }

    @Override
    public List<Mensaje> obtenerConversacion(Integer idUsuario1, Integer idUsuario2) {
        return mensajeRepository.findConversacionEntreUsuarios(idUsuario1, idUsuario2);
    }

    @Override
    public List<Mensaje> obtenerMensajesPorSolicitud(Integer idSolicitud) {
        return mensajeRepository.findByIdSolicitud(idSolicitud);
    }

    @Override
    public Mensaje marcarComoLeido(Integer idMensaje) {
        Mensaje m = mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        m.setLeido(true);
        return mensajeRepository.save(m);
    }
}
