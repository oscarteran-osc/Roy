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
import com.example.roy.models.UserProfileResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        // Inicializar API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Vincular vistas
        initViews();

        // Obtener datos de sesión
        obtenerDatosSesion();

        // Obtener ID del arrendador desde el Intent
        if (getIntent() != null) {
            arrendadorId = getIntent().getIntExtra("arrendadorId", -1);
            Log.d(TAG, "ArrendadorId recibido: " + arrendadorId);
        }

        if (arrendadorId == -1) {
            Toast.makeText(this, "Error: ID de arrendador no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar listeners
        setupListeners();

        // Cargar datos del arrendador
        cargarPerfilArrendador();
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
                    Toast.makeText(PerfilArrendadorActivity.this,
                            "Error al cargar el perfil del arrendador",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(PerfilArrendadorActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void mostrarDatosPerfil(UserProfileResponse perfil) {
        // Nombre completo
        String nombre = obtenerTextoSeguro(perfil.getNombre());
        String apellido = obtenerTextoSeguro(perfil.getApellido());
        String nombreCompleto = (nombre + " " + apellido).trim();

        tvNombreArr.setText(nombreCompleto.isEmpty() ? "Arrendador" : nombreCompleto);

        // Región/Zona
        tvRegionArr.setText(obtenerTextoSeguro(perfil.getZona(), "CDMX"));

        // Reputación (calificación promedio)
        Double reputacion = perfil.getReputacion();
        if (reputacion == null) {
            reputacion = 0.0;
        }
        tvReputacionPromedioArr.setText(String.format(Locale.US, "⭐ %.1f", reputacion));

        // Total de reseñas
        tvTotalResenasArr.setText("");

        // Foto de perfil con Glide
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
                if (response.isSuccessful() && response.body() != null) {
                    List<Objeto> objetos = response.body();
                    Log.d(TAG, "Objetos del arrendador cargados: " + objetos.size());
                    mostrarObjetosArrendador(objetos);
                } else {
                    Log.e(TAG, "Error al cargar objetos: " + response.code());
                    mostrarObjetosArrendador(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar objetos: " + t.getMessage());
                mostrarObjetosArrendador(new ArrayList<>());
            }
        });
    }

    private void mostrarObjetosArrendador(List<Objeto> objetos) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvObjetosArrendador.setLayoutManager(layoutManager);

        if (objetos.isEmpty()) {
            Toast.makeText(this,
                    "Este arrendador no tiene objetos publicados",
                    Toast.LENGTH_SHORT).show();
        }

        ObjetosArrendadorAdapter adapter = new ObjetosArrendadorAdapter(objetos, objeto -> {
            // Click en un objeto - abrir Activity de detalles
            navigateToObjetoDetalle(objeto.getIdObjeto());
        });

        rvObjetosArrendador.setAdapter(adapter);
    }

    private void navigateToObjetoDetalle(int objetoId) {
        Intent intent = new Intent(this, Objetoo.class);
        intent.putExtra("objetoId", objetoId);
        startActivity(intent);
    }

    private void enviarCalificacion() {
        float calificacion = rbCalificarArr.getRating();
        String comentario = etComentarioArr.getText().toString().trim();

        if (calificacion == 0) {
            Toast.makeText(this,
                    "Por favor selecciona una calificación",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1 || token == null) {
            Toast.makeText(this,
                    "Debes iniciar sesión para calificar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == arrendadorId) {
            Toast.makeText(this,
                    "No puedes calificarte a ti mismo",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implementar el endpoint para enviar la calificación
        Toast.makeText(this,
                "Calificación enviada: " + calificacion + " estrellas",
                Toast.LENGTH_SHORT).show();

        // Limpiar el formulario
        rbCalificarArr.setRating(0);
        etComentarioArr.setText("");

        // Recargar el perfil
        cargarPerfilArrendador();
    }

    private String obtenerTextoSeguro(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private String obtenerTextoSeguro(String texto, String valorPorDefecto) {
        if (texto == null || texto.trim().isEmpty()) {
            return valorPorDefecto;
        }
        return texto.trim();
    }
}