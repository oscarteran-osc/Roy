package com.example.roy.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.roy.Inicio;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private ApiService apiService;
    private EditText etMail, etContra;
    private Button btnIniciar;
    private TextView goBackRegister, tvOlvideContrasena;
    private ProgressBar progressBar;
    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        etMail            = findViewById(R.id.loginmail);
        etContra          = findViewById(R.id.logincontra);
        btnIniciar        = findViewById(R.id.btnlogin);
        goBackRegister    = findViewById(R.id.backregister);
        progressBar       = findViewById(R.id.progressBar);
        tvOlvideContrasena = findViewById(R.id.tvOlvideContrasena);

        progressBar.setVisibility(View.GONE);

        btnIniciar.setOnClickListener(this);
        goBackRegister.setOnClickListener(this);
        tvOlvideContrasena.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnlogin) {
            realizarLogin();
        } else if (id == R.id.backregister) {
            startActivity(new Intent(this, Registro.class));
        } else if (id == R.id.tvOlvideContrasena) {
            mostrarDialogRecuperarContrasena();
        }
    }

    private void mostrarDialogRecuperarContrasena() {
        View dialogView = getLayoutInflater().inflate(android.R.layout.activity_list_item, null);

        EditText etCorreo = new EditText(this);
        etCorreo.setHint("Correo electrónico");
        etCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etCorreo.setPadding(40, 20, 40, 20);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setMessage("Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.")
            .setView(etCorreo)
            .setPositiveButton("Enviar", null)
            .setNegativeButton("Cancelar", null)
            .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String correo = etCorreo.getText().toString().trim();
                if (correo.isEmpty() || !correo.contains("@")) {
                    etCorreo.setError("Ingresa un correo válido");
                    return;
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Enviando...");
                enviarCorreoRecuperacion(correo, dialog);
            });
        });

        dialog.show();
    }

    private void enviarCorreoRecuperacion(String correo, AlertDialog dialog) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("correo", correo);

                String baseUrl = RetrofitClient.getClient().baseUrl().toString();
                Request request = new Request.Builder()
                    .url(baseUrl + "auth/recuperar-password")
                    .post(RequestBody.create(
                        MediaType.parse("application/json"),
                        body.toString()
                    ))
                    .build();

                try (okhttp3.Response response = httpClient.newCall(request).execute()) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this,
                            "Si el correo está registrado, recibirás un enlace en breve.",
                            Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("Enviar");
                    Toast.makeText(this, "No se pudo conectar. Verifica tu internet.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void realizarLogin() {
        String email    = etMail.getText().toString().trim();
        String password = etContra.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIniciar.setEnabled(false);
        btnIniciar.setText("Iniciando...");
        progressBar.setVisibility(View.VISIBLE);

        LoginRequest credenciales = new LoginRequest(email, password);

        apiService.loginUser(credenciales).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Integer userId = authResponse.getIdUsuario();
                    String token   = authResponse.getToken();

                    if (userId != null && token != null && !token.trim().isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
                        prefs.edit()
                            .putInt("userId", userId)
                            .putString("token", token)
                            .putString("userName", authResponse.getNombre())
                            .putString("userEmail", authResponse.getCorreo())
                            .apply();

                        Toast.makeText(LoginActivity.this,
                            "¡Bienvenido " + authResponse.getNombre() + "!",
                            Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, Inicio.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                            "El servidor no devolvió datos válidos.",
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                        "Credenciales incorrectas.",
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_LONG).show();
            }
        });
    }
}
