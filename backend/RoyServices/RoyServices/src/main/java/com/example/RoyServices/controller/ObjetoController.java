package com.example.RoyServices.controller;

import com.example.RoyServices.dto.ObjetoDto;
import com.example.RoyServices.model.Objeto;
import com.example.RoyServices.service.ObjetoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/objeto")
@RestController
@AllArgsConstructor
public class ObjetoController {
    private final ObjetoService objetoService;

    @GetMapping("/objeto")
    public ResponseEntity<List<ObjetoDto>> lista() {
        List<Objeto> objetos = objetoService.getAll();
        if (objetos == null || objetos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                objetos.stream()
                        .map(o -> ObjetoDto.builder()
                                .idObjeto(o.getIdObjeto())
                                .idUsArrendador(o.getIdUsArrendador())
                                .nombreObjeto(o.getNombreObjeto())
                                .precio(o.getPrecio())
                                .estado(o.getEstado())
                                .categoria(o.getCategoria())
                                .descripcion(o.getDescripcion())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> getById(@PathVariable Integer id) {
        Objeto o = objetoService.getById(id);
        if (o == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ObjetoDto.builder()
                .idObjeto(o.getIdObjeto())
                .idUsArrendador(o.getIdUsArrendador())
                .nombreObjeto(o.getNombreObjeto())
                .precio(o.getPrecio())
                .estado(o.getEstado())
                .categoria(o.getCategoria())
                .descripcion(o.getDescripcion())
                .build());
    }

    @PostMapping("/objeto")
    public ResponseEntity<ObjetoDto> save(@RequestBody ObjetoDto dto) {
        Objeto o = Objeto.builder()
                .idUsArrendador(dto.getIdUsArrendador())
                .nombreObjeto(dto.getNombreObjeto())
                .precio(dto.getPrecio())
                .estado(dto.getEstado())
                .categoria(dto.getCategoria())
                .descripcion(dto.getDescripcion())
                .build();
        objetoService.save(o);
        return ResponseEntity.ok(ObjetoDto.builder()
                .idObjeto(o.getIdObjeto())
                .idUsArrendador(o.getIdUsArrendador())
                .nombreObjeto(o.getNombreObjeto())
                .precio(o.getPrecio())
                .estado(o.getEstado())
                .categoria(o.getCategoria())
                .descripcion(o.getDescripcion())
                .build());
    }

    @DeleteMapping("/objeto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        objetoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> update(@PathVariable Integer id, @RequestBody ObjetoDto dto) {
        Objeto o = Objeto.builder()
                .idUsArrendador(dto.getIdUsArrendador())
                .nombreObjeto(dto.getNombreObjeto())
                .precio(dto.getPrecio())
                .estado(dto.getEstado())
                .categoria(dto.getCategoria())
                .descripcion(dto.getDescripcion())
                .build();
        Objeto aux = objetoService.update(id, o);
        return ResponseEntity.ok(ObjetoDto.builder()
                .idObjeto(aux.getIdObjeto())
                .idUsArrendador(aux.getIdUsArrendador())
                .nombreObjeto(aux.getNombreObjeto())
                .precio(aux.getPrecio())
                .estado(aux.getEstado())
                .categoria(aux.getCategoria())
                .descripcion(aux.getDescripcion())
                .build());
    }
}