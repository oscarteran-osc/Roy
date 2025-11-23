package com.example.RoyServices.controller;
import com.example.RoyServices.dto.ImagenRequest;
import com.example.RoyServices.model.ImagenObjeto;
import com.example.RoyServices.service.ImagenObjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenObjetoController {

    private final ImagenObjetoService imagenObjetoService;

    public ImagenObjetoController(ImagenObjetoService imagenObjetoService) {
        this.imagenObjetoService = imagenObjetoService;
    }

    @PostMapping
    public ResponseEntity<ImagenObjeto> crearImagen(@RequestBody ImagenRequest request) {
        ImagenObjeto imagen = imagenObjetoService.crearImagen(request);
        return ResponseEntity.ok(imagen);
    }


    @GetMapping("/objeto/{idObjeto}")
    public ResponseEntity<List<ImagenObjeto>> listarPorObjeto(@PathVariable Integer idObjeto) {
        return ResponseEntity.ok(imagenObjetoService.obtenerImagenesPorObjeto(idObjeto));
    }

    @PatchMapping("/{idImagen}/principal")
    public ResponseEntity<ImagenObjeto> marcarPrincipal(@PathVariable Integer idImagen) {
        return ResponseEntity.ok(imagenObjetoService.marcarComoPrincipal(idImagen));
    }

    @DeleteMapping("/{idImagen}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idImagen) {
        imagenObjetoService.eliminarImagen(idImagen);
        return ResponseEntity.noContent().build();
    }
}
