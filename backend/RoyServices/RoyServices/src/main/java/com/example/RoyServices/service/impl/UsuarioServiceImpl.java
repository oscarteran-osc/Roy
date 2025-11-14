package com.example.RoyServices.service.impl;
import com.example.RoyServices.service.UsuarioService;
import com.example.RoyServices.repository.UsuarioRepository;
import com.example.RoyServices.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service

public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Override

    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario getById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario update(Integer id, Usuario usuario) {
        Usuario aux = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        aux.setNombre(usuario.getNombre());
        aux.setApellido(usuario.getApellido());
        aux.setCorreo(usuario.getCorreo());
        aux.setTelefono(usuario.getTelefono());
        aux.setDomicilio(usuario.getDomicilio());
        aux.setFechaDeRegistro(usuario.getFechaDeRegistro());
        aux.setPassword(usuario.getPassword());

        return usuarioRepository.save(aux);
    }
}
