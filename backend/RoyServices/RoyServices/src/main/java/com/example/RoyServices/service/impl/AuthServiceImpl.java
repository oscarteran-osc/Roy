package com.example.RoyServices.service.impl;

import com.example.RoyServices.dto.AuthResponse;
import com.example.RoyServices.dto.LoginRequest;
import com.example.RoyServices.dto.RegisterRequest;
import com.example.RoyServices.model.Usuario;
import com.example.RoyServices.repository.UsuarioRepository;
import com.example.RoyServices.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        // IMPORTANTE: Usar passwordEncoder.matches() para comparar la contraseña cifrada
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
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
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .domicilio(request.getDomicilio())  // ✅ Asegúrate que sea getDomicilio()
                .password(passwordEncoder.encode(request.getPassword()))
                .zona(request.getZona()) // o "Sin zona" o lo que uses
                .fotoUrl(null)
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
        response.setToken(UUID.randomUUID().toString()); // Generación de token simple
        return response;
    }
}