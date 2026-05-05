package com.example.roy.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.login.LoginActivity;
import com.example.roy.login.MainActivity;
import com.example.roy.home.Objetoo;
import com.example.roy.models.Objeto;
import com.example.roy.models.SolicitudRenta;
import com.example.roy.models.UserProfileResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    // UI Components
    private ShapeableImageView imgAvatar;
    private TextView tvNombre;
    private TextView tvRegion;
    private Chip chipReputacion;
    private TextView tvTelefono;
    private TextView tvEmail;
    private TextView tvPassword;
    private MaterialButton btnEditarPerfil;
    private RecyclerView rvHistorial;
    private TextView tvSinHistorial;

    // API
    private ApiService apiService;
    private Button btnLogout;

    // Datos de sesión
    private int userId;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Vincular vistas
        initViews(view);

        // Obtener datos de sesión
        if (!obtenerDatosSesion()) {
            redirigirALogin();
            return view;
        }

        // Configurar listeners
        setupListeners();

        // Configurar RecyclerView de historial
        setupHistorialRecyclerView();
        btnLogout.setOnClickListener(v -> mostrarDialogoLogout());

        // Cargar datos del perfil desde el servidor
        cargarPerfil();

        return view;
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvRegion = view.findViewById(R.id.tvRegion);
        chipReputacion = view.findViewById(R.id.chipReputacion);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvPassword = view.findViewById(R.id.tvPassword);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        rvHistorial = view.findViewById(R.id.rvHistorial);
        tvSinHistorial = view.findViewById(R.id.tvSinHistorial);
    }

    private boolean obtenerDatosSesion() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        token = prefs.getString("token", null);

        return userId != -1 && token != null;
    }

    private void setupListeners() {
        btnEditarPerfil.setOnClickListener(v -> {
            // Navegar a EditarPerfilFragment
            Fragment editarFragment = new EditarPerfil();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });

        imgAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Cambiar foto (próximamente)", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarPerfil() {
        String auth = "Bearer " + token;

        apiService.getPerfil(userId, auth).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarDatosPerfil(response.body());
                    // ✅ Cargar promedio real de calificaciones recibidas
                    cargarPromedioCalificacion();
                } else {
                    manejarErrorCarga(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarPromedioCalificacion() {
        apiService.getPromedioCalificacionUsuario(userId).enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call,
                                   Response<java.util.Map<String, Object>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Object> data = response.body();
                    double promedio = 0.0;
                    Object promedioObj = data.get("promedioCalificacion");
                    if (promedioObj instanceof Number) {
                        promedio = ((Number) promedioObj).doubleValue();
                    }
                    chipReputacion.setText("⭐ " + String.format(java.util.Locale.US, "%.1f", promedio));
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                Log.e("PERFIL", "Error al cargar promedio: " + t.getMessage());
            }
        });
    }

    private void mostrarDatosPerfil(UserProfileResponse perfil) {
        // Nombre completo
        String nombre = obtenerTextoSeguro(perfil.getNombre());
        String apellido = obtenerTextoSeguro(perfil.getApellido());
        String nombreCompleto = (nombre + " " + apellido).trim();

        tvNombre.setText(nombreCompleto.isEmpty() ? "Usuario" : nombreCompleto);

        // Región/Zona
        String zona = obtenerTextoSeguro(perfil.getZona(), "CDMX");
        tvRegion.setText(zona);

        // ✅ Guardar zona en SharedPreferences para el filtro "Cerca de ti"
        requireActivity().getSharedPreferences("RoyPrefs", MODE_PRIVATE)
                .edit().putString("zona", zona).apply();

        // Reputación — se actualiza con cargarPromedioCalificacion()
        chipReputacion.setText("⭐ 0.0");

        // Teléfono - formateado
        String telefono = obtenerTextoSeguro(perfil.getTelefono(), "No registrado");
        if (!telefono.equals("No registrado") && telefono.length() == 10) {
            telefono = telefono.substring(0, 2) + " " +
                    telefono.substring(2, 6) + " " +
                    telefono.substring(6);
        }
        tvTelefono.setText(telefono);

        // Email
        tvEmail.setText(obtenerTextoSeguro(perfil.getCorreo(), "Sin correo"));

        // Contraseña - nunca mostrar la real
        tvPassword.setText("••••••••");

        // Foto de perfil con Glide
        cargarFotoPerfil(perfil.getFotoUrl());
    }

    private void cargarFotoPerfil(String fotoUrl) {
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(fotoUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.profile);
        }
    }

    private void setupHistorialRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvHistorial.setLayoutManager(layoutManager);

        // ✅ Cargar objetos desde el API
        cargarHistorialObjetos();
    }

    private void cargarHistorialObjetos() {
        // ✅ Cargar solicitudes PAGADAS del usuario (objetos que ha rentado a otros)
        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudRenta> solicitudes = response.body();
                    Log.d("PERFIL_DEBUG", "📦 Solicitudes cargadas: " + solicitudes.size());

                    // Filtrar solo las que fueron pagadas (historial real de rentas)
                    List<ItemHistorial> historial = new ArrayList<>();
                    for (SolicitudRenta s : solicitudes) {
                        if ("PAGADA".equalsIgnoreCase(s.getEstado())) {
                            historial.add(new ItemHistorial(
                                    s.getNombreObjeto() != null ? s.getNombreObjeto() : "Objeto #" + s.getIdObjeto(),
                                    "$" + s.getMonto(),
                                    s.getImagenObjeto(),
                                    s.getIdObjeto()
                            ));
                        }
                    }
                    mostrarHistorialDesdeItems(historial);
                } else {
                    Log.e("PERFIL_DEBUG", "❌ Error al cargar solicitudes: " + response.code());
                    mostrarHistorialVacio();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("PERFIL_DEBUG", "💥 Error de conexión: " + t.getMessage());
                mostrarHistorialVacio();
            }
        });
    }

    private void mostrarHistorialDesdeItems(List<ItemHistorial> historial) {
        if (historial == null || historial.isEmpty()) {
            mostrarHistorialVacio();
            return;
        }

        rvHistorial.setVisibility(View.VISIBLE);
        tvSinHistorial.setVisibility(View.GONE);

        HistorialAdapter adapter = new HistorialAdapter(historial, item -> {
            // ✅ Navegar al detalle del objeto al hacer click
            Intent intent = new Intent(requireContext(), Objetoo.class);
            intent.putExtra("objetoId", item.getObjetoId());
            startActivity(intent);
        });

        rvHistorial.setAdapter(adapter);
    }

    private void mostrarHistorial(List<Objeto> objetos) {
        if (objetos == null || objetos.isEmpty()) {
            mostrarHistorialVacio();
            return;
        }

        List<ItemHistorial> historial = new ArrayList<>();
        for (Objeto objeto : objetos) {
            historial.add(new ItemHistorial(
                    objeto.getNombreObjeto(),
                    "$" + objeto.getPrecio(),
                    objeto.getImagenUrl(),
                    objeto.getIdObjeto()
            ));
        }

        mostrarHistorialDesdeItems(historial);
    }

    private void mostrarHistorialVacio() {
        rvHistorial.setVisibility(View.GONE);
        tvSinHistorial.setVisibility(View.VISIBLE);
    }

    private void manejarErrorCarga(int codigoError) {
        String mensaje;

        if (codigoError == 401) {
            mensaje = "Sesión expirada. Inicia sesión nuevamente.";
            redirigirALogin();
        } else if (codigoError == 404) {
            mensaje = "Perfil no encontrado.";
        } else {
            mensaje = "Error al cargar perfil. Código: " + codigoError;
        }

        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private void redirigirALogin() {
        // ✅ Limpiar sesión
        SharedPreferences prefs = requireActivity().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // ✅ Ir a MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
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

    public static class ItemHistorial {
        private String nombre;
        private String precio;
        private String imagenUrl;
        private int objetoId;

        public ItemHistorial(String nombre, String precio, String imagenUrl, int objetoId) {
            this.nombre = nombre;
            this.precio = precio;
            this.imagenUrl = imagenUrl;
            this.objetoId = objetoId;
        }

        public String getNombre() { return nombre; }
        public String getPrecio() { return precio; }
        public String getImagenUrl() { return imagenUrl; }
        public int getObjetoId() { return objetoId; }
    }

    public interface OnHistorialClickListener {
        void onClick(ItemHistorial item);
    }

    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí, cerrar", (dialog, which) -> realizarLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarLogout() {
        // ✅ Limpiar SharedPreferences completamente
        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // ✅ Redirigir a MainActivity con flags correctos
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
