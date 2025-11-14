package com.example.demo.service;

import com.example.demo.model.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> getAll( );
    Usuario getById(Integer id);
    Usuario save(Usuario usuario);
    void delete(Integer id);
    Usuario update(Integer id, Usuario usuario);
}
