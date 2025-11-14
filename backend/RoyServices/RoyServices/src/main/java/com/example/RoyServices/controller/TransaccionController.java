package com.example.RoyServices.controller;

import com.example.RoyServices.dto.TransaccionDto;
import com.example.RoyServices.model.Transaccion;
import com.example.RoyServices.service.TransaccionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/transaccion")
@RestController
@AllArgsConstructor
public class TransaccionController {
    private final TransaccionService transaccionService;

    @GetMapping("/transaccion")
    public ResponseEntity<List<TransaccionDto>> lista() {
        List<Transaccion> transacciones = transaccionService.getAll();
        if (transacciones == null || transacciones.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                transacciones.stream()
                        .map(t -> TransaccionDto.builder()
                                .idTransaccion(t.getIdTransaccion())
                                .montoTotal(t.getMontoTotal())
                                .fechaPago(t.getFechaPago())
                                .metodoPago(t.getMetodoPago())
                                .estatusPago(t.getEstatusPago())
                                .idSolicitud(t.getIdSolicitud())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/transaccion/{id}")
    public ResponseEntity<TransaccionDto> getById(@PathVariable Integer id) {
        Transaccion t = transaccionService.getById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(TransaccionDto.builder()
                .idTransaccion(t.getIdTransaccion())
                .montoTotal(t.getMontoTotal())
                .fechaPago(t.getFechaPago())
                .metodoPago(t.getMetodoPago())
                .estatusPago(t.getEstatusPago())
                .idSolicitud(t.getIdSolicitud())
                .build());
    }

    @PostMapping("/transaccion")
    public ResponseEntity<TransaccionDto> save(@RequestBody TransaccionDto dto) {
        Transaccion t = Transaccion.builder()
                .montoTotal(dto.getMontoTotal())
                .fechaPago(dto.getFechaPago())
                .metodoPago(dto.getMetodoPago())
                .estatusPago(dto.getEstatusPago())
                .idSolicitud(dto.getIdSolicitud())
                .build();
        transaccionService.save(t);
        return ResponseEntity.ok(TransaccionDto.builder()
                .idTransaccion(t.getIdTransaccion())
                .montoTotal(t.getMontoTotal())
                .fechaPago(t.getFechaPago())
                .metodoPago(t.getMetodoPago())
                .estatusPago(t.getEstatusPago())
                .idSolicitud(t.getIdSolicitud())
                .build());
    }

    @DeleteMapping("/transaccion/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        transaccionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transaccion/{id}")
    public ResponseEntity<TransaccionDto> update(@PathVariable Integer id, @RequestBody TransaccionDto dto) {
        Transaccion t = Transaccion.builder()
                .montoTotal(dto.getMontoTotal())
                .fechaPago(dto.getFechaPago())
                .metodoPago(dto.getMetodoPago())
                .estatusPago(dto.getEstatusPago())
                .idSolicitud(dto.getIdSolicitud())
                .build();
        Transaccion aux = transaccionService.update(id, t);
        return ResponseEntity.ok(TransaccionDto.builder()
                .idTransaccion(aux.getIdTransaccion())
                .montoTotal(aux.getMontoTotal())
                .fechaPago(aux.getFechaPago())
                .metodoPago(aux.getMetodoPago())
                .estatusPago(aux.getEstatusPago())
                .idSolicitud(aux.getIdSolicitud())
                .build());
    }
}