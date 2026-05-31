package com.example.RoyServices.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/mensaje")
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = Map.of(
                "model", "claude-sonnet-4-20250514",
                "max_tokens", 400,
                "system", body.get("system"),
                "messages", body.get("messages")
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                request,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                String respuesta = content != null && !content.isEmpty()
                    ? (String) content.get(0).get("text")
                    : "No pude generar una respuesta.";
                return ResponseEntity.ok(Map.of("respuesta", respuesta));
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Error al contactar al asistente."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
}
