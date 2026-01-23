package com.example.roy.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.home.ObjetosArrendadorAdapter;
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

public class perfilArrendador extends Fragment {

    private static final String TAG = "PerfilArrendador";

    // UI Components del XML
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_arrendador, container, false);

        // Inicializar API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Vincular vistas
        initViews(view);

        // Obtener datos de sesión
        obtenerDatosSesion();

        // Obtener ID del arrendador desde los argumentos
        if (getArguments() != null) {
            arrendadorId = getArguments().getInt("arrendadorId", -1);
            Log.d(TAG, "ArrendadorId recibido: " + arrendadorId);
        }

        if (arrendadorId == -1) {
            Toast.makeText(requireContext(), "Error: ID de arrendador no válido", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return view;
        }

        // Configurar listeners
        setupListeners();

        // Cargar datos del arrendador
        cargarPerfilArrendador();
        cargarObjetosArrendador();

        return view;
    }

    private void initViews(View view) {
        imgAvatarArr = view.findViewById(R.id.imgAvatarArr);
        tvNombreArr = view.findViewById(R.id.tvNombreArr);
        tvRegionArr = view.findViewById(R.id.tvRegionArr);
        tvReputacionPromedioArr = view.findViewById(R.id.tvReputacionPromedioArr);
        tvTotalResenasArr = view.findViewById(R.id.tvTotalResenasArr);
        rbCalificarArr = view.findViewById(R.id.rbCalificarArr);
        etComentarioArr = view.findViewById(R.id.etComentarioArr);
        btnEnviarCalificacionArr = view.findViewById(R.id.btnEnviarCalificacionArr);
        rvObjetosArrendador = view.findViewById(R.id.rvObjetosArrendador);
    }

    private void obtenerDatosSesion() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("RoyPrefs", android.content.Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
        token = prefs.getString("token", null);
    }

    private void setupListeners() {
        btnEnviarCalificacionArr.setOnClickListener(v -> enviarCalificacion());
    }

    private void cargarPerfilArrendador() {
        String auth = token != null ? "Bearer " + token : "";

        apiService.getPerfil(arrendadorId, auth).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    perfilArrendador = response.body();
                    mostrarDatosPerfil(perfilArrendador);
                } else {
                    Toast.makeText(requireContext(),
                            "Error al cargar el perfil del arrendador",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(requireContext(),
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

        // Total de reseñas (puedes obtener esto del backend)
        // Por ahora lo dejamos vacío o puedes calcularlo
        tvTotalResenasArr.setText(""); // O algo como "(10 reseñas)"

        // Foto de perfil con Glide
        cargarFotoPerfil(perfil.getFotoUrl());
    }

    private void cargarFotoPerfil(String fotoUrl) {
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(requireContext())
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
                if (!isAdded()) return;

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
                if (!isAdded()) return;

                Log.e(TAG, "Error de conexión al cargar objetos: " + t.getMessage());
                mostrarObjetosArrendador(new ArrayList<>());
            }
        });
    }

    private void mostrarObjetosArrendador(List<Objeto> objetos) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvObjetosArrendador.setLayoutManager(layoutManager);

        if (objetos.isEmpty()) {
            // Mostrar mensaje de que no hay objetos
            Toast.makeText(requireContext(),
                    "Este arrendador no tiene objetos publicados",
                    Toast.LENGTH_SHORT).show();
        }

        // Usar el adapter de objetos del arrendador
        ObjetosArrendadorAdapter adapter = new ObjetosArrendadorAdapter(objetos, objeto -> {
            // Click en un objeto - navegar a detalles
            navigateToObjetoDetalle(objeto.getIdObjeto());
        });

        rvObjetosArrendador.setAdapter(adapter);
    }

    private void navigateToObjetoDetalle(int objetoId) {
        // Navegar al fragment de detalles del objeto
        com.example.roy.home.objetito detalleFragment =
                com.example.roy.home.objetito.newInstance(objetoId);

        // CORRECCIÓN: Usar el ID correcto del contenedor
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorfragmentos, detalleFragment)
                .addToBackStack(null)
                .commit();
    }

    private void enviarCalificacion() {
        float calificacion = rbCalificarArr.getRating();
        String comentario = etComentarioArr.getText().toString().trim();

        // Validar que haya seleccionado una calificación
        if (calificacion == 0) {
            Toast.makeText(requireContext(),
                    "Por favor selecciona una calificación",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el usuario esté logueado
        if (currentUserId == -1 || token == null) {
            Toast.makeText(requireContext(),
                    "Debes iniciar sesión para calificar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que no se esté calificando a sí mismo
        if (currentUserId == arrendadorId) {
            Toast.makeText(requireContext(),
                    "No puedes calificarte a ti mismo",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implementar el endpoint para enviar la calificación
        // Por ahora solo mostramos un mensaje
        Toast.makeText(requireContext(),
                "Calificación enviada: " + calificacion + " estrellas",
                Toast.LENGTH_SHORT).show();

        // Limpiar el formulario
        rbCalificarArr.setRating(0);
        etComentarioArr.setText("");

        // Recargar el perfil para actualizar la reputación
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

    public static perfilArrendador newInstance(int arrendadorId) {
        perfilArrendador fragment = new perfilArrendador();
        Bundle args = new Bundle();
        args.putInt("arrendadorId", arrendadorId);
        fragment.setArguments(args);
        return fragment;
    }
}