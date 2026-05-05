package com.example.roy.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.home.ObjetosArrendadorAdapter;
import com.example.roy.home.Objetoo;
import com.example.roy.models.Objeto;
import com.example.roy.models.Resena;
import com.example.roy.models.UserProfileResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilArrendadorActivity extends AppCompatActivity {

    private static final String TAG = "PerfilArrendadorActivity";

    // UI Components
    private ImageButton btnBack;
    private ShapeableImageView imgAvatarArr;
    private TextView tvNombreArr;
    private TextView tvRegionArr;
    private TextView tvReputacionPromedioArr;
    private TextView tvTotalResenasArr;
    private RatingBar rbCalificarArr;
    private EditText etComentarioArr;
    private MaterialButton btnEnviarCalificacionArr;
    private RecyclerView rvObjetosArrendador;

    // API y datos
    private ApiService apiService;
    private int arrendadorId = -1;
    private int currentUserId = -1;
    private String token;
    private UserProfileResponse perfilArrendador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_arrendador);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViews();
        obtenerDatosSesion();

        if (getIntent() != null) {
            arrendadorId = getIntent().getIntExtra("arrendadorId", -1);
        }

        if (arrendadorId == -1) {
            Toast.makeText(this, "Error: ID de arrendador no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupListeners();
        cargarPerfilArrendador();
        cargarPromedioCalificacion();
        cargarObjetosArrendador();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackPerfil);
        imgAvatarArr = findViewById(R.id.imgAvatarArr);
        tvNombreArr = findViewById(R.id.tvNombreArr);
        tvRegionArr = findViewById(R.id.tvRegionArr);
        tvReputacionPromedioArr = findViewById(R.id.tvReputacionPromedioArr);
        tvTotalResenasArr = findViewById(R.id.tvTotalResenasArr);
        rbCalificarArr = findViewById(R.id.rbCalificarArr);
        etComentarioArr = findViewById(R.id.etComentarioArr);
        btnEnviarCalificacionArr = findViewById(R.id.btnEnviarCalificacionArr);
        rvObjetosArrendador = findViewById(R.id.rvObjetosArrendador);
    }

    private void obtenerDatosSesion() {
        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
        token = prefs.getString("token", null);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEnviarCalificacionArr.setOnClickListener(v -> enviarCalificacion());
    }

    private void cargarPerfilArrendador() {
        String auth = token != null ? "Bearer " + token : "";

        apiService.getPerfil(arrendadorId, auth).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilArrendador = response.body();
                    mostrarDatosPerfil(perfilArrendador);
                } else {
                    Log.e(TAG, "Error al cargar perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
            }
        });
    }

    /** Carga el promedio real desde el endpoint de reseñas */
    private void cargarPromedioCalificacion() {
        apiService.getPromedioCalificacionUsuario(arrendadorId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();

                    double promedio = 0.0;
                    long total = 0;

                    Object promedioObj = data.get("promedioCalificacion");
                    if (promedioObj instanceof Number) {
                        promedio = ((Number) promedioObj).doubleValue();
                    }

                    Object totalObj = data.get("totalResenas");
                    if (totalObj instanceof Number) {
                        total = ((Number) totalObj).longValue();
                    }

                    final double promedioFinal = promedio;
                    final long totalFinal = total;

                    runOnUiThread(() -> {
                        if (tvReputacionPromedioArr != null) {
                            tvReputacionPromedioArr.setText(
                                    String.format(Locale.US, "⭐ %.1f", promedioFinal));
                        }
                        if (tvTotalResenasArr != null) {
                            tvTotalResenasArr.setText(
                                    totalFinal + (totalFinal == 1 ? " reseña" : " reseñas"));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error al cargar promedio: " + t.getMessage());
            }
        });
    }

    private void mostrarDatosPerfil(UserProfileResponse perfil) {
        String nombre = perfil.getNombre() != null ? perfil.getNombre().trim() : "";
        String apellido = perfil.getApellido() != null ? perfil.getApellido().trim() : "";
        String nombreCompleto = (nombre + " " + apellido).trim();

        tvNombreArr.setText(nombreCompleto.isEmpty() ? "Arrendador" : nombreCompleto);
        tvRegionArr.setText(perfil.getZona() != null ? perfil.getZona() : "CDMX");

        cargarFotoPerfil(perfil.getFotoUrl());
    }

    private void cargarFotoPerfil(String fotoUrl) {
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.profileuser)
                    .error(R.drawable.profileuser)
                    .into(imgAvatarArr);
        } else {
            imgAvatarArr.setImageResource(R.drawable.profileuser);
        }
    }

    private void cargarObjetosArrendador() {
        apiService.getObjetosPorArrendador(arrendadorId).enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                List<Objeto> objetos = (response.isSuccessful() && response.body() != null)
                        ? response.body() : new ArrayList<>();
                mostrarObjetosArrendador(objetos);
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                mostrarObjetosArrendador(new ArrayList<>());
            }
        });
    }

    private void mostrarObjetosArrendador(List<Objeto> objetos) {
        rvObjetosArrendador.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ObjetosArrendadorAdapter adapter = new ObjetosArrendadorAdapter(objetos, objeto ->
                startActivity(new Intent(this, Objetoo.class)
                        .putExtra("objetoId", objeto.getIdObjeto()))
        );
        rvObjetosArrendador.setAdapter(adapter);
    }

    private void enviarCalificacion() {
        float calificacion = rbCalificarArr.getRating();
        String comentario = etComentarioArr.getText().toString().trim();

        if (calificacion == 0) {
            Toast.makeText(this, "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1 || token == null) {
            Toast.makeText(this, "Debes iniciar sesión para calificar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == arrendadorId) {
            Toast.makeText(this, "No puedes calificarte a ti mismo", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Construir la reseña de usuario (idObjeto = null, idUsReceptor = arrendadorId)
        Resena resena = new Resena();
        resena.setIdObjeto(null);          // Es calificación de usuario, no de objeto
        resena.setIdUsAutor(currentUserId);
        resena.setIdUsReceptor(arrendadorId);
        resena.setCalificacion((int) calificacion);
        resena.setComentario(comentario.isEmpty() ? null : comentario);

        btnEnviarCalificacionArr.setEnabled(false);
        btnEnviarCalificacionArr.setText("Enviando...");

        apiService.crearResena(resena).enqueue(new Callback<Resena>() {
            @Override
            public void onResponse(Call<Resena> call, Response<Resena> response) {
                btnEnviarCalificacionArr.setEnabled(true);
                btnEnviarCalificacionArr.setText("Enviar calificación");

                if (response.isSuccessful()) {
                    Toast.makeText(PerfilArrendadorActivity.this,
                            "✅ Calificación enviada", Toast.LENGTH_SHORT).show();

                    rbCalificarArr.setRating(0);
                    etComentarioArr.setText("");

                    // ✅ Recargar el promedio actualizado
                    cargarPromedioCalificacion();
                } else {
                    String msg = response.code() == 400
                            ? "Ya calificaste a este usuario"
                            : "Error al enviar: " + response.code();
                    Toast.makeText(PerfilArrendadorActivity.this, msg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al calificar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Resena> call, Throwable t) {
                btnEnviarCalificacionArr.setEnabled(true);
                btnEnviarCalificacionArr.setText("Enviar calificación");
                Toast.makeText(PerfilArrendadorActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
