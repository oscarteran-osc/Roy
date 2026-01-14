package com.example.RoyServices.controller;

import com.example.RoyServices.model.Notificacion;
import com.example.RoyServices.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/notificaciones")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(@PathVariable Integer idUsuario) {
        List<Notificacion> lista = notificacionService.getNotificacionesUsuario(idUsuario);
        if (lista == null || lista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<List<Notificacion>> listarNoLeidas(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(notificacionService.getNoLeidas(idUsuario));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@PathVariable Integer idUsuario,
                                              @RequestBody Map<String, String> body) {
        String tipo = body.get("tipo");
        String mensaje = body.get("mensaje");
        return ResponseEntity.ok(notificacionService.crearNotificacion(idUsuario, tipo, mensaje));
    }

    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable Integer idNotificacion) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(idNotificacion));
    }
}
