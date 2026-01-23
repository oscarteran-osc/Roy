package com.example.RoyServices.controller;

import com.example.RoyServices.dto.ResenaDto;
import com.example.RoyServices.model.Resena;
import com.example.RoyServices.model.Usuario;
import com.example.RoyServices.service.ResenaService;
import com.example.RoyServices.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resenas")
@AllArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<ResenaDto>> getAll() {
        List<Resena> resenas = resenaService.getAll();
        if (resenas.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(resenas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDto> getById(@PathVariable Integer id) {
        Resena resena = resenaService.getById(id);
        if (resena == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(resena));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ResenaDto dto) {
        try {
            Resena resena = Resena.builder()
                    .idObjeto(dto.getIdObjeto())
                    .idUsAutor(dto.getIdUsAutor())
                    .idUsReceptor(dto.getIdUsReceptor())
                    .calificacion(dto.getCalificacion())
                    .comentario(dto.getComentario())
                    .fechaResena(dto.getFechaResena() != null ? dto.getFechaResena() : LocalDate.now())
                    .build();

            Resena guardada = resenaService.save(resena);
            return ResponseEntity.ok(convertirADto(guardada));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ResenaDto dto) {
        try {
            Resena resena = Resena.builder()
                    .calificacion(dto.getCalificacion())
                    .comentario(dto.getComentario())
                    .fechaResena(dto.getFechaResena())
                    .build();

            Resena actualizada = resenaService.update(id, resena);

            if (actualizada == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(convertirADto(actualizada));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        resenaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<ResenaDto>> getPorAutor(@PathVariable Integer idAutor) {
        List<Resena> resenas = resenaService.getResenasPorAutor(idAutor);
        if (resenas.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(resenas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/receptor/{idReceptor}")
    public ResponseEntity<List<ResenaDto>> getPorReceptor(@PathVariable Integer idReceptor) {
        List<Resena> resenas = resenaService.getResenasPorReceptor(idReceptor);
        if (resenas.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(resenas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/objeto/{idObjeto}")
    public ResponseEntity<List<ResenaDto>> getPorObjeto(@PathVariable Integer idObjeto) {
        List<Resena> resenas = resenaService.getResenasPorObjeto(idObjeto);
        return ResponseEntity.ok(
                resenas.stream().map(this::convertirADto).collect(Collectors.toList())
        );
    }

    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<ResenaDto>> getPorCalificacion(@PathVariable Integer calificacion) {
        List<Resena> resenas = resenaService.getResenasPorCalificacion(calificacion);
        return ResponseEntity.ok(resenas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<ResenaDto>> getPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        List<Resena> resenas = resenaService.getResenasPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(resenas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/promedio/{idUsuario}")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Integer idUsuario) {
        Double promedio = resenaService.getPromedioCalificaciones(idUsuario);
        Long total = resenaService.contarResenasRecibidas(idUsuario);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("idUsuario", idUsuario);
        resultado.put("promedioCalificacion", promedio);
        resultado.put("totalResenas", total);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarResena(
            @RequestParam Integer idAutor,
            @RequestParam Integer idReceptor
    ) {
        boolean yaReseno = resenaService.usuarioYaResenoA(idAutor, idReceptor);
        return ResponseEntity.ok(Map.of("yaReseno", yaReseno));
    }

    private ResenaDto convertirADto(Resena resena) {
        String nombreAutor = "Usuario";
        String nombreReceptor = "Usuario";

        try {
            Usuario autor = usuarioService.getById(resena.getIdUsAutor());
            if (autor != null) {
                nombreAutor = autor.getNombre() + " " + autor.getApellido();
            }
        } catch (Exception e) {
            // Mantener "Usuario" por defecto
        }

        try {
            Usuario receptor = usuarioService.getById(resena.getIdUsReceptor());
            if (receptor != null) {
                nombreReceptor = receptor.getNombre() + " " + receptor.getApellido();
            }
        } catch (Exception e) {
            // Mantener "Usuario" por defecto
        }

        return ResenaDto.builder()
                .idResena(resena.getIdResena())
                .idObjeto(resena.getIdObjeto())
                .idUsAutor(resena.getIdUsAutor())
                .idUsReceptor(resena.getIdUsReceptor())
                .calificacion(resena.getCalificacion())
                .comentario(resena.getComentario())
                .fechaResena(resena.getFechaResena())
                .nombreAutor(nombreAutor)
                .nombreReceptor(nombreReceptor)
                .build();
    }
}
