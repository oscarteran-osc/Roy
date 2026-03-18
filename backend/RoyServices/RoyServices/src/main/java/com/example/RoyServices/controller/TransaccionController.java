package com.example.RoyServices.controller;

import com.example.RoyServices.model.Transaccion;
import com.example.RoyServices.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    @Autowired
    private TransaccionRepository transaccionRepository;

    // Obtener todas las transacciones
    @GetMapping
    public ResponseEntity<List<Transaccion>> obtenerTodasLasTransacciones() {
        try {
            List<Transaccion> transacciones = transaccionRepository.findAll();
            return ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener transacción por ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerTransaccionPorId(@PathVariable Integer id) {
        try {
            Optional<Transaccion> transaccion = transaccionRepository.findById(id);
            return transaccion.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener transacciones por ID de solicitud
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Transaccion>> obtenerTransaccionesPorSolicitud(
            @PathVariable Integer idSolicitud) {
        try {
            List<Transaccion> transacciones = transaccionRepository.findByIdSolicitud(idSolicitud);
            return ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener transacciones por estado de pago
    @GetMapping("/estado/{estatusPago}")
    public ResponseEntity<List<Transaccion>> obtenerTransaccionesPorEstado(
            @PathVariable String estatusPago) {
        try {
            List<Transaccion> transacciones = transaccionRepository.findByEstatusPago(estatusPago);
            return ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Crear una nueva transacción
    @PostMapping
    public ResponseEntity<Transaccion> crearTransaccion(@RequestBody Transaccion transaccion) {
        try {
            Transaccion nuevaTransaccion = transaccionRepository.save(transaccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTransaccion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Actualizar una transacción
    @PutMapping("/{id}")
    public ResponseEntity<Transaccion> actualizarTransaccion(
            @PathVariable Integer id,
            @RequestBody Transaccion transaccionActualizada) {
        try {
            Optional<Transaccion> transaccionExistente = transaccionRepository.findById(id);

            if (transaccionExistente.isPresent()) {
                Transaccion transaccion = transaccionExistente.get();

                // Actualizar los campos
                if (transaccionActualizada.getMontoTotal() != null) {
                    transaccion.setMontoTotal(transaccionActualizada.getMontoTotal());
                }
                if (transaccionActualizada.getFechaPago() != null) {
                    transaccion.setFechaPago(transaccionActualizada.getFechaPago());
                }
                if (transaccionActualizada.getMetodoPago() != null) {
                    transaccion.setMetodoPago(transaccionActualizada.getMetodoPago());
                }
                if (transaccionActualizada.getEstatusPago() != null) {
                    transaccion.setEstatusPago(transaccionActualizada.getEstatusPago());
                }
                if (transaccionActualizada.getIdSolicitud() != null) {
                    transaccion.setIdSolicitud(transaccionActualizada.getIdSolicitud());
                }

                Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
                return ResponseEntity.ok(transaccionGuardada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Eliminar una transacción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Integer id) {
        try {
            Optional<Transaccion> transaccion = transaccionRepository.findById(id);

            if (transaccion.isPresent()) {
                transaccionRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}