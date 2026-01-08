package com.example.RoyServices.controller;
import com.example.RoyServices.dto.ImagenRequest;
import com.example.RoyServices.model.ImagenObjeto;
import com.example.RoyServices.service.ImagenObjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = "*")
public class ImagenObjetoController {

    private final ImagenObjetoService imagenObjetoService;

        @Value("${app.upload-dir:uploads}")
        private String uploadDir;

        @PostMapping("/subir")
        public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
            try {
                // 1) Nombre único
                String nombreArchivo = UUID.randomUUID() + "-" + file.getOriginalFilename();

                // 2) Ruta física
                Path directorio = Paths.get(uploadDir);
                if (!Files.exists(directorio)) {
                    Files.createDirectories(directorio);
                }

                Path rutaArchivo = directorio.resolve(nombreArchivo);

                // 3) Guardar archivo
                Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

                // ⚠ Importante:
                // Guardamos SOLO la ruta relativa, no el host, para no mezclarnos con localhost / 10.0.2.2
                String rutaRelativa = "/uploads/" + nombreArchivo;

                // Esto es lo que debes guardar en la BD en imagenUrl
                return ResponseEntity.ok(rutaRelativa);

            } catch (IOException e) {
                return ResponseEntity.internalServerError()
                        .body("Error al subir la imagen: " + e.getMessage());
            }
        }


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
