package com.example.roy.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class ChatbotActivity extends AppCompatActivity {

    // Usa el backend de ROY como intermediario (evita problemas de red y seguridad)
    private static final String BACKEND_URL = "http://10.0.2.2:8080/api/chatbot/mensaje";
    private static final String SYSTEM_PROMPT =
        "Eres ROY Assistant, el asistente virtual de ROY (plataforma de renta de objetos). " +
        "Responde SIEMPRE en español, de forma amable y breve. Solo responde preguntas sobre ROY.\n\n" +
        "Información sobre ROY:\n" +
        "- Para rentar: busca en el feed, entra al detalle y toca 'Enviar Solicitud'. Elige fechas y confirma.\n" +
        "- Para publicar: ve a 'Mis Objetos' → botón '+' → llena los datos del objeto.\n" +
        "- El pago es con PayPal desde Solicitudes → botón 'Pagar'.\n" +
        "- Solicitudes: PENDIENTE → APROBADA → PAGADA.\n" +
        "- Puedes chatear con el otro usuario desde el botón 💬 en solicitudes.\n" +
        "- Las calificaciones son de 1 a 5 estrellas.\n" +
        "- Tu historial de rentas aparece en tu perfil.\n" +
        "Si preguntan algo fuera de ROY, diles amablemente que solo puedes ayudar con la plataforma.";

    private TextView tvMensajes;
    private EditText etMensaje;
    private ScrollView scrollView;
    private final List<JSONObject> historial = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        tvMensajes = findViewById(R.id.tvMensajes);
        etMensaje  = findViewById(R.id.etMensaje);
        scrollView = findViewById(R.id.scrollView);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnEnviarBot).setOnClickListener(v -> enviarMensaje());

        etMensaje.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensaje();
            return true;
        });

        agregarMensaje("🤖 ¡Hola! Soy el asistente de ROY. ¿En qué puedo ayudarte?", false);
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;
        etMensaje.setText("");

        agregarMensaje("Tú: " + texto, true);

        try {
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", texto);
            historial.add(userMsg);
        } catch (Exception e) { e.printStackTrace(); }

        agregarMensaje("🤖 Escribiendo...", false);

        new Thread(() -> {
            try {
                JSONArray messages = new JSONArray();
                for (JSONObject msg : historial) messages.put(msg);

                JSONObject body = new JSONObject();
                body.put("system", SYSTEM_PROMPT);
                body.put("messages", messages);

                Request request = new Request.Builder()
                        .url(BACKEND_URL)
                        .post(RequestBody.create(
                                MediaType.parse("application/json"),
                                body.toString()
                        ))
                        .addHeader("Content-Type", "application/json")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    JSONObject json = new JSONObject(responseBody);
                    String respuesta = json.getString("respuesta");

                    JSONObject assistantMsg = new JSONObject();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", respuesta);
                    historial.add(assistantMsg);

                    // Limitar historial a últimos 10 mensajes
                    if (historial.size() > 10) historial.subList(0, historial.size() - 10).clear();

                    runOnUiThread(() -> {
                        String actual = tvMensajes.getText().toString();
                        int idx = actual.lastIndexOf("🤖 Escribiendo...");
                        if (idx >= 0) {
                            tvMensajes.setText(actual.substring(0, idx).trim());
                        }
                        agregarMensaje("🤖 " + respuesta, false);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    String actual = tvMensajes.getText().toString();
                    int idx = actual.lastIndexOf("🤖 Escribiendo...");
                    if (idx >= 0) tvMensajes.setText(actual.substring(0, idx).trim());
                    agregarMensaje("🤖 Lo siento, tuve un error. Intenta de nuevo.", false);
                });
            }
        }).start();
    }

    private void agregarMensaje(String texto, boolean esMio) {
        runOnUiThread(() -> {
            String actual = tvMensajes.getText().toString();
            tvMensajes.setText(actual.isEmpty() ? texto : actual + "\n\n" + texto);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }
}
