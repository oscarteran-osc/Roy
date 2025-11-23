package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.ImagenRequest;
import com.example.RoyServices.model.ImagenObjeto;
import com.example.RoyServices.model.Objeto;
import com.example.RoyServices.repository.ImagenObjetoRepository;
import com.example.RoyServices.repository.ObjetoRepository;
import com.example.RoyServices.service.ImagenObjetoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ImagenObjetoServiceImpl implements ImagenObjetoService {

    private final ImagenObjetoRepository imagenObjetoRepository;
    private final ObjetoRepository objetoRepository;

    @Override
    @Transactional
    public ImagenObjeto crearImagen(ImagenRequest request) {
        Objeto objeto = objetoRepository.findById(request.getIdObjeto())
                .orElseThrow(() -> new RuntimeException("Objeto no encontrado"));

        ImagenObjeto imagen = new ImagenObjeto();
        imagen.setIdObjeto(objeto.getIdObjeto()); // 👈 AQUÍ EL CAMBIO
        imagen.setUrlImagen(request.getUrlImagen());
        imagen.setEsPrincipal(Boolean.TRUE.equals(request.getEsPrincipal()));

        // Si es principal, desmarco las otras
        if (Boolean.TRUE.equals(request.getEsPrincipal())) {
            List<ImagenObjeto> actuales = imagenObjetoRepository.findByIdObjeto(objeto.getIdObjeto());
            for (ImagenObjeto img : actuales) {
                img.setEsPrincipal(false);
            }
            imagenObjetoRepository.saveAll(actuales);
        }

        return imagenObjetoRepository.save(imagen);
    }

    @Override
    public List<ImagenObjeto> obtenerImagenesPorObjeto(Integer idObjeto) {
        return imagenObjetoRepository.findByIdObjeto(idObjeto);
    }

    @Override
    @Transactional
    public ImagenObjeto marcarComoPrincipal(Integer idImagen) {
        ImagenObjeto imagen = imagenObjetoRepository.findById(idImagen)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        List<ImagenObjeto> actuales = imagenObjetoRepository.findByIdObjeto(imagen.getIdObjeto());
        for (ImagenObjeto img : actuales) {
            img.setEsPrincipal(false);
        }
        imagenObjetoRepository.saveAll(actuales);

        imagen.setEsPrincipal(true);
        return imagenObjetoRepository.save(imagen);
    }

    @Override
    public void eliminarImagen(Integer idImagen) {
        if (!imagenObjetoRepository.existsById(idImagen)) {
            throw new RuntimeException("Imagen no encontrada");
        }
        imagenObjetoRepository.deleteById(idImagen);
    }
}
