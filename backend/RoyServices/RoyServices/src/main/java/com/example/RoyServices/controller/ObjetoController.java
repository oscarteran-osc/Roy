package com.example.RoyServices.controller;

import com.example.RoyServices.dto.ObjetoConZonaProjection;
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

    @GetMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> getById(@PathVariable Integer id) {
        try {
            ObjetoConZonaProjection o = objetoService.getByIdConZona(id);
            if (o == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDto(o));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/objeto")
    public ResponseEntity<ObjetoDto> save(@RequestBody ObjetoDto dto) {
        try {
            Objeto o = Objeto.builder()
                    .idUsArrendador(dto.getIdUsArrendador())
                    .nombreObjeto(dto.getNombreObjeto())
                    .precio(dto.getPrecio())
                    .estado(dto.getEstado())
                    .categoria(dto.getCategoria())
                    .descripcion(dto.getDescripcion())
                    .imagenUrl(dto.getImagenUrl())
                    .build();

            objetoService.save(o);

            ObjetoConZonaProjection creado = objetoService.getByIdConZona(o.getIdObjeto());
            if (creado == null) {
                return ResponseEntity.ok(ObjetoDto.builder()
                        .idObjeto(o.getIdObjeto())
                        .idUsArrendador(o.getIdUsArrendador())
                        .nombreObjeto(o.getNombreObjeto())
                        .precio(o.getPrecio())
                        .estado(o.getEstado())
                        .categoria(o.getCategoria())
                        .descripcion(o.getDescripcion())
                        .imagenUrl(o.getImagenUrl())
                        .zona(null)
                        .nomArrendador(null)
                        .build());
            }
            return ResponseEntity.ok(toDto(creado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/objeto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            objetoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/arrendador/{arrendadorId}")
    public ResponseEntity<List<ObjetoDto>> getObjetosPorArrendador(@PathVariable Integer arrendadorId) {
        try {
            List<ObjetoConZonaProjection> objetos = objetoService.buscarPorArrendadorConZona(arrendadorId);

            if (objetos == null || objetos.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            return ResponseEntity.ok(
                    objetos.stream()
                            .map(this::toDto)
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> update(@PathVariable Integer id, @RequestBody ObjetoDto dto) {
        try {
            Objeto o = Objeto.builder()
                    .idUsArrendador(dto.getIdUsArrendador())
                    .nombreObjeto(dto.getNombreObjeto())
                    .precio(dto.getPrecio())
                    .estado(dto.getEstado())
                    .categoria(dto.getCategoria())
                    .descripcion(dto.getDescripcion())
                    .imagenUrl(dto.getImagenUrl())
                    .build();

            Objeto aux = objetoService.update(id, o);
            if (aux == null) return ResponseEntity.notFound().build();

            ObjetoConZonaProjection actualizado = objetoService.getByIdConZona(aux.getIdObjeto());
            if (actualizado == null) return ResponseEntity.notFound().build();

            return ResponseEntity.ok(toDto(actualizado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ObjetoDto>> buscar(@RequestParam("q") String texto) {
        try {
            List<ObjetoConZonaProjection> objetos = objetoService.buscarPorTextoConZona(texto);
            if (objetos == null || objetos.isEmpty()) return ResponseEntity.ok(List.of());
            return ResponseEntity.ok(objetos.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<ObjetoDto>> porCategoria(@RequestParam("nombre") String categoria) {
        try {
            List<ObjetoConZonaProjection> objetos = objetoService.buscarPorCategoriaConZona(categoria);
            if (objetos == null || objetos.isEmpty()) return ResponseEntity.ok(List.of());
            return ResponseEntity.ok(objetos.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/recomendados")
    public ResponseEntity<List<ObjetoDto>> recomendados() {
        try {
            List<ObjetoConZonaProjection> objetos = objetoService.obtenerRecomendadosConZona();
            if (objetos == null || objetos.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            List<ObjetoConZonaProjection> top = objetos.size() > 10 ? objetos.subList(0, 10) : objetos;
            return ResponseEntity.ok(top.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/destacado")
    public ResponseEntity<ObjetoDto> destacado(@RequestParam(value = "userId", required = false) Integer userId) {
        try {
            ObjetoConZonaProjection o = objetoService.obtenerDestacadoConZona(userId);
            if (o == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(toDto(o));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/objeto")
    public ResponseEntity<List<ObjetoDto>> lista() {
        try {
            List<ObjetoConZonaProjection> objetos = objetoService.getAllConZona();
            if (objetos == null || objetos.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(objetos.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private ObjetoDto toDto(ObjetoConZonaProjection o) {
        return ObjetoDto.builder()
                .idObjeto(o.getIdObjeto())
                .idUsArrendador(o.getIdUsArrendador())
                .nombreObjeto(o.getNombreObjeto())
                .precio(o.getPrecio())
                .estado(o.getEstado())
                .categoria(o.getCategoria())
                .descripcion(o.getDescripcion())
                .imagenUrl(o.getImagenUrl())
                .zona(o.getZona())
                .nomArrendador(o.getNomArrendador())
                .build();
    }
}