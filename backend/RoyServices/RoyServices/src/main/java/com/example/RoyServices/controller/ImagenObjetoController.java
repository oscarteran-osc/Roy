package com.example.RoyServices.controller;

import com.example.RoyServices.model.ImagenObjeto;
import com.example.RoyServices.repository.ImagenObjetoRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagen")
@AllArgsConstructor
public class ImagenObjetoController {

    private final ImagenObjetoRepository imagenObjetoRepository;

    @GetMapping("/objeto/{objetoId}")
    public ResponseEntity<List<ImagenObjeto>> getImagenesObjeto(@PathVariable Integer objetoId) {
        List<ImagenObjeto> imagenes = imagenObjetoRepository.findByIdObjeto(objetoId);
        return ResponseEntity.ok(imagenes);
    }
}