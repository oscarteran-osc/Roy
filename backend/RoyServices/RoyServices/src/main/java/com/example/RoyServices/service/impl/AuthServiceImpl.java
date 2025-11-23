package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.AuthResponse;
import com.example.RoyServices.dto.LoginRequest;
import com.example.RoyServices.dto.RegisterRequest;
import com.example.RoyServices.model.Usuario;
import com.example.RoyServices.repository.UsuarioRepository;
import com.example.RoyServices.service.AuthService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        if (!usuario.getPassword().equals(request.getContrasena())) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        return buildAuthResponse(usuario);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombreUs())
                .apellido(request.getApellidoUs())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .domicilio(request.getDomicilio())
                .password(request.getContrasena())
                .fechaDeRegistro(LocalDate.now())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        return buildAuthResponse(guardado);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        AuthResponse response = new AuthResponse();
        response.setIdUsuario(usuario.getIdUsuario());
        response.setNombre(usuario.getNombre() + " " + usuario.getApellido());
        response.setCorreo(usuario.getCorreo());
        response.setToken(UUID.randomUUID().toString());
        return response;
    }
}
