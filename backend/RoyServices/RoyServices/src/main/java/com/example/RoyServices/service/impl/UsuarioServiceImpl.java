package com.example.RoyServices.service.impl;
import com.example.RoyServices.service.UsuarioService;
import com.example.RoyServices.repository.UsuarioRepository;
import com.example.RoyServices.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service

public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (usuario.getNombre() != null) aux.setNombre(usuario.getNombre());
        if (usuario.getApellido() != null) aux.setApellido(usuario.getApellido());
        if (usuario.getCorreo() != null) aux.setCorreo(usuario.getCorreo());
        if (usuario.getTelefono() != null) aux.setTelefono(usuario.getTelefono());
        if (usuario.getDomicilio() != null) aux.setDomicilio(usuario.getDomicilio());
        if (usuario.getFechaDeRegistro() != null) aux.setFechaDeRegistro(usuario.getFechaDeRegistro());
        if (usuario.getZona() != null) aux.setZona(usuario.getZona());
        if (usuario.getFotoUrl() != null) aux.setFotoUrl(usuario.getFotoUrl());

        // ✅ Encodear contraseña con BCrypt antes de guardar
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            aux.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(aux);
    }
}
