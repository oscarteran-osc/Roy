package com.example.RoyServices.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/mensaje")
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            // Armar mensajes: system primero, luego historial
            List<Map<String, Object>> messages = new ArrayList<>();
            Object systemPrompt = body.get("system");
            if (systemPrompt != null) {
                messages.add(Map.of("role", "system", "content", systemPrompt.toString()));
            }
            Object historialObj = body.get("messages");
            if (historialObj instanceof List<?> historial) {
                for (Object msg : historial) {
                    if (msg instanceof Map<?, ?> msgMap) {
                        messages.add(Map.of(
                            "role", String.valueOf(msgMap.get("role")),
                            "content", String.valueOf(msgMap.get("content"))
                        ));
                    }
                }
            }

            Map<String, Object> requestBody = Map.of(
                "model", "llama3-8b-8192",
                "max_tokens", 400,
                "messages", messages
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                request,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String respuesta = message != null ? (String) message.get("content") : "Sin respuesta.";
                    return ResponseEntity.ok(Map.of("respuesta", respuesta));
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "El asistente no pudo responder."));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Error de Groq (4xx o 5xx)
            System.err.println("Error Groq: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Error del asistente: " + e.getStatusCode()));
        } catch (Exception e) {
            System.err.println("Error chatbot: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
}
