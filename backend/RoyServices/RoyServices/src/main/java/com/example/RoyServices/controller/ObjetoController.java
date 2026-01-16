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

    // ✅ LISTA (con zona)
    @GetMapping("/objeto")
    public ResponseEntity<List<ObjetoDto>> lista() {
        List<ObjetoConZonaProjection> objetos = objetoService.getAllConZona();
        if (objetos == null || objetos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                objetos.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    // ✅ GET BY ID (con zona)
    @GetMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> getById(@PathVariable Integer id) {
        ObjetoConZonaProjection o = objetoService.getByIdConZona(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(o));
    }

    // ✅ SAVE (zona NO se manda; se deriva)
    @PostMapping("/objeto")
    public ResponseEntity<ObjetoDto> save(@RequestBody ObjetoDto dto) {
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

        // volvemos a pedirlo con zona para devolver respuesta completa
        ObjetoConZonaProjection creado = objetoService.getByIdConZona(o.getIdObjeto());
        if (creado == null) {
            // fallback sin zona (por si algo raro)
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
                    .build());
        }
        return ResponseEntity.ok(toDto(creado));
    }

    @DeleteMapping("/objeto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        objetoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ UPDATE (zona NO se manda; se deriva)
    @PutMapping("/objeto/{id}")
    public ResponseEntity<ObjetoDto> update(@PathVariable Integer id, @RequestBody ObjetoDto dto) {

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

        // devolver actualizado con zona
        ObjetoConZonaProjection actualizado = objetoService.getByIdConZona(aux.getIdObjeto());
        if (actualizado == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(toDto(actualizado));
    }

    // ----------------- Home endpoints (con zona) -----------------

    @GetMapping("/buscar")
    public ResponseEntity<List<ObjetoDto>> buscar(@RequestParam("q") String texto) {
        List<ObjetoConZonaProjection> objetos = objetoService.buscarPorTextoConZona(texto);
        if (objetos == null || objetos.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(objetos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/categoria")
    public ResponseEntity<List<ObjetoDto>> porCategoria(@RequestParam("nombre") String categoria) {
        List<ObjetoConZonaProjection> objetos = objetoService.buscarPorCategoriaConZona(categoria);
        if (objetos == null || objetos.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(objetos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/recomendados")
    public ResponseEntity<List<ObjetoDto>> recomendados() {
        List<ObjetoConZonaProjection> objetos = objetoService.obtenerRecomendadosConZona();
        if (objetos == null || objetos.isEmpty()) return ResponseEntity.notFound().build();

        // si quieres solo 10:
        List<ObjetoConZonaProjection> top = objetos.size() > 10 ? objetos.subList(0, 10) : objetos;

        return ResponseEntity.ok(top.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/destacado")
    public ResponseEntity<ObjetoDto> destacado() {
        ObjetoConZonaProjection o = objetoService.obtenerDestacadoConZona();
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(o));
    }

    // ----------------- Mapper -----------------

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
                .zona(o.getZona()) // ✅ AQUI VA
                .build();
    }
}
