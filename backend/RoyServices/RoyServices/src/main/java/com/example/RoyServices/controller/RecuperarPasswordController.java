package com.example.RoyServices.controller;

import com.example.RoyServices.model.TokenRecuperacion;
import com.example.RoyServices.model.Usuario;
import com.example.RoyServices.repository.TokenRecuperacionRepository;
import com.example.RoyServices.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class RecuperarPasswordController {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    public RecuperarPasswordController(UsuarioRepository usuarioRepository,
                                       TokenRecuperacionRepository tokenRepository,
                                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository   = tokenRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    @Transactional
    @PostMapping("/recuperar-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Correo requerido."));

        if (!usuarioRepository.existsByCorreo(correo))
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirás un enlace en breve."));

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

        // Enviar correo via API HTTP de Brevo (no SMTP)
        try {
            String jsonBody = "{"
                + "\"sender\": {\"name\": \"ROY App\", \"email\": \"AdvancedTechProgramming@outlook.com\"},"
                + "\"to\": [{\"email\": \"" + correo + "\"}],"
                + "\"subject\": \"ROY – Recupera tu contraseña\","
                + "\"textContent\": \"Hola,\\n\\nRecibimos una solicitud para restablecer tu contraseña en ROY.\\n\\nHaz clic en el siguiente enlace (válido por 1 hora):\\n" + link + "\\n\\nSi no solicitaste esto, puedes ignorar este correo.\\n\\n– El equipo de ROY\""
                + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .header("accept", "application/json")
                .header("api-key", brevoApiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Brevo API response: " + response.statusCode() + " " + response.body());
        } catch (Exception e) {
            System.err.println("❌ Error enviando correo via Brevo API: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirás un enlace en breve."));
    }

    @Transactional
    @PostMapping("/resetear-password")
    public ResponseEntity<?> resetearPassword(@RequestBody Map<String, String> body) {
        String token         = body.get("token");
        String nuevaPassword = body.get("nuevaPassword");

        if (token == null || nuevaPassword == null || nuevaPassword.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos. La contraseña debe tener al menos 6 caracteres."));

        TokenRecuperacion tokenObj = tokenRepository.findByToken(token).orElse(null);
        if (tokenObj == null || tokenObj.isUsado())
            return ResponseEntity.badRequest().body(Map.of("error", "El enlace no es válido o ya fue usado."));

        if (LocalDateTime.now().isAfter(tokenObj.getFechaExpiracion()))
            return ResponseEntity.badRequest().body(Map.of("error", "El enlace expiró. Solicita uno nuevo."));

        Usuario usuario = usuarioRepository.findByCorreo(tokenObj.getCorreo()).orElse(null);
        if (usuario == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado."));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        tokenObj.setUsado(true);
        tokenRepository.save(tokenObj);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
    }
}
