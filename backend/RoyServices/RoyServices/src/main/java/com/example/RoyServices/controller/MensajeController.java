package com.example.RoyServices.controller;

import com.example.RoyServices.dto.MensajeRequest;
import com.example.RoyServices.model.Mensaje;
import com.example.RoyServices.service.MensajeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class MensajeController {

    private final MensajeService mensajeService;

    // GET todos
    @GetMapping
    public ResponseEntity<List<Mensaje>> getAll() {
        List<Mensaje> mensajes = mensajeService.getAll();
        if (mensajes == null || mensajes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mensajes);
    }

    // GET por id
    @GetMapping("/{id}")
    public ResponseEntity<Mensaje> getById(@PathVariable Integer id) {
        Mensaje m = mensajeService.getById(id);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(m);
    }

    // POST enviar
    @PostMapping("/enviar/{idRemitente}")
    public ResponseEntity<Mensaje> enviarMensaje(@PathVariable Integer idRemitente,
                                                 @RequestBody MensajeRequest request) {
        return ResponseEntity.ok(mensajeService.enviarMensaje(idRemitente, request));
    }

    // GET conversación entre dos usuarios
    @GetMapping("/conversacion")
    public ResponseEntity<List<Mensaje>> obtenerConversacion(
            @RequestParam Integer idUsuario1,
            @RequestParam Integer idUsuario2) {
        return ResponseEntity.ok(mensajeService.obtenerConversacion(idUsuario1, idUsuario2));
    }

    // GET mensajes por solicitud
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Mensaje>> obtenerPorSolicitud(@PathVariable Integer idSolicitud) {
        return ResponseEntity.ok(mensajeService.obtenerMensajesPorSolicitud(idSolicitud));
    }

    // PATCH marcar leído
    @PatchMapping("/{idMensaje}/leido")
    public ResponseEntity<Mensaje> marcarComoLeido(@PathVariable Integer idMensaje) {
        return ResponseEntity.ok(mensajeService.marcarComoLeido(idMensaje));
    }
}

