package com.example.RoyServices.service;

import com.example.RoyServices.model.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll( );
    Usuario getById(Integer idUsuario);
    Usuario save(Usuario usuario);
    void delete(Integer idUsuario);
    Usuario update(Integer idUsuario, Usuario usuario);
}
