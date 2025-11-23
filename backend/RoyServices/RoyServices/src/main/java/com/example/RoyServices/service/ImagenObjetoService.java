package com.example.RoyServices.service;

import com.example.RoyServices.dto.ImagenRequest;
import com.example.RoyServices.model.ImagenObjeto;

import java.util.List;

public interface ImagenObjetoService {

    ImagenObjeto crearImagen(ImagenRequest request);

    List<ImagenObjeto> obtenerImagenesPorObjeto(Integer idObjeto);

    ImagenObjeto marcarComoPrincipal(Integer idImagen);

    void eliminarImagen(Integer idImagen);
}


