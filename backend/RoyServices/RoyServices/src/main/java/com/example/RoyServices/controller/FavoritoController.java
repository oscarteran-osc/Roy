package com.example.RoyServices.controller;

import com.example.RoyServices.dto.FavoritoRequest;
import com.example.RoyServices.model.Favorito;
import com.example.RoyServices.service.FavoritoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/favoritos")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping
    public ResponseEntity<List<Favorito>> listar(@PathVariable Integer idUsuario) {
        List<Favorito> favoritos = favoritoService.getFavoritosPorUsuario(idUsuario);
        if (favoritos == null || favoritos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping
    public ResponseEntity<Favorito> agregar(@PathVariable Integer idUsuario,
                                            @RequestBody FavoritoRequest request) {
        return ResponseEntity.ok(favoritoService.agregarFavorito(idUsuario, request));
    }

    @DeleteMapping("/{idObjeto}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idUsuario,
                                         @PathVariable Integer idObjeto) {
        favoritoService.eliminarFavorito(idUsuario, idObjeto);
        return ResponseEntity.noContent().build();
    }
}
