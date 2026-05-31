package com.example.RoyServices.controller;

import com.example.RoyServices.model.TokenRecuperacion;
import com.example.RoyServices.model.Usuario;
import com.example.RoyServices.repository.TokenRecuperacionRepository;
import com.example.RoyServices.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class RecuperarPasswordController {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String correoOrigen;

    public RecuperarPasswordController(UsuarioRepository usuarioRepository,
                                       TokenRecuperacionRepository tokenRepository,
                                       JavaMailSender mailSender,
                                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository   = tokenRepository;
        this.mailSender        = mailSender;
        this.passwordEncoder   = passwordEncoder;
    }

    // POST /auth/recuperar-password  { "correo": "..." }
    @Transactional
    @PostMapping("/recuperar-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Correo requerido."));

        // Siempre respondemos igual para no revelar si el correo existe
        if (!usuarioRepository.existsByCorreo(correo))
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirás un enlace en breve."));

        // Borrar tokens anteriores del mismo correo
        tokenRepository.deleteByCorreo(correo);

        String token = UUID.randomUUID().toString();
        TokenRecuperacion tokenObj = TokenRecuperacion.builder()
                .token(token)
                .correo(correo)
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .usado(false)
                .build();
        tokenRepository.save(tokenObj);

        String link = baseUrl + "/recuperar-password.html?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("AdvancedTechProgramming@outlook.com");
        mensaje.setTo(correo);
        mensaje.setSubject("ROY – Recupera tu contraseña");
        mensaje.setText(
            "Hola,\n\n" +
            "Recibimos una solicitud para restablecer tu contraseña en ROY.\n\n" +
            "Haz clic en el siguiente enlace (válido por 1 hora):\n" +
            link + "\n\n" +
            "Si no solicitaste esto, puedes ignorar este correo.\n\n" +
            "– El equipo de ROY"
        );

        try {
            mailSender.send(mensaje);
            System.out.println("✅ Correo de recuperación enviado a: " + correo);
        } catch (Exception emailEx) {
            System.err.println("❌ Error al enviar correo: " + emailEx.getMessage());
            emailEx.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirás un enlace en breve."));
    }

    // POST /auth/resetear-password  { "token": "...", "nuevaPassword": "..." }
    @Transactional
    @PostMapping("/resetear-password")
    public ResponseEntity<?> resetearPassword(@RequestBody Map<String, String> body) {
        String token          = body.get("token");
        String nuevaPassword  = body.get("nuevaPassword");

        if (token == null || nuevaPassword == null || nuevaPassword.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos. La contraseña debe tener al menos 6 caracteres."));

        TokenRecuperacion tokenObj = tokenRepository.findByToken(token)
                .orElse(null);

        if (tokenObj == null || tokenObj.isUsado())
            return ResponseEntity.badRequest().body(Map.of("error", "El enlace no es válido o ya fue usado."));

        if (LocalDateTime.now().isAfter(tokenObj.getFechaExpiracion()))
            return ResponseEntity.badRequest().body(Map.of("error", "El enlace expiró. Solicita uno nuevo."));

        Usuario usuario = usuarioRepository.findByCorreo(tokenObj.getCorreo())
                .orElse(null);

        if (usuario == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado."));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        tokenObj.setUsado(true);
        tokenRepository.save(tokenObj);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
    }
}
