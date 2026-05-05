package com.example.roy.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.example.roy.models.SolicitudRenta;
import com.example.roy.models.SolicitudRentaRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Objetoo extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ObjetooActivity";

    ImageButton atrasito;
    Button solicitud;
    private int objetoId = -1;
    private Objeto objetoActual;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_objeto);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        atrasito = findViewById(R.id.back);
        solicitud = findViewById(R.id.enviosoli);

        atrasito.setOnClickListener(this);
        solicitud.setOnClickListener(this);

        // Obtener el ID del objeto del Intent
        if (getIntent() != null) {
            objetoId = getIntent().getIntExtra("objetoId", -1);
            Log.d(TAG, "objetoId recibido: " + objetoId);
        }

        if (objetoId == -1) {
            Toast.makeText(this, "Error: ID de objeto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos del objeto para verificar disponibilidad
        cargarDatosObjeto();

        // Cargar el fragment objetito con el ID
        if (savedInstanceState == null) {
            objetito fragment = objetito.newInstance(objetoId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorfragmentos, fragment)
                    .commit();
        }
    }

    private void cargarDatosObjeto() {
        apiService.getObjetoPorId(objetoId).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetoActual = response.body();
                    Log.d(TAG, "Objeto cargado: " + objetoActual.getNombreObjeto());
                    Log.d(TAG, "Estado disponibilidad: " + objetoActual.getEstado());

                    // Verificar si el objeto es del usuario actual
                    verificarPropietario();
                } else {
                    Log.e(TAG, "Error al cargar objeto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    private void verificarPropietario() {
        SharedPreferences prefs = getSharedPreferences("ROY_PREFS", MODE_PRIVATE);
        int miUsuarioId = prefs.getInt("userId", -1);

        // Si el objeto es del usuario actual, ocultar el botón de solicitud
        if (objetoActual != null && objetoActual.getIdUsArrendador() == miUsuarioId) {
            solicitud.setVisibility(View.GONE);
            Log.d(TAG, "Objeto propio - botón de solicitud oculto");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.enviosoli) {
            enviarSolicitud();
        }
    }

    private void enviarSolicitud() {
        if (objetoActual == null) {
            Toast.makeText(this, "Cargando información del objeto...", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean disponible = esDisponible(objetoActual.getEstado());
        if (!disponible) {
            Toast.makeText(this, "Este objeto no está disponible actualmente",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        int miUsuarioId = prefs.getInt("userId", -1);

        if (miUsuarioId == -1) {
            Toast.makeText(this, "❌ Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (objetoActual.getIdUsArrendador() == miUsuarioId) {
            Toast.makeText(this, "No puedes solicitar tu propio objeto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Mostrar diálogo para elegir fechas
        mostrarDialogoFechas(miUsuarioId);
    }

    private void mostrarDialogoFechas(int miUsuarioId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View dialogView = android.view.LayoutInflater.from(this).inflate(
                android.R.layout.simple_list_item_2, null);

        // Crear layout programáticamente para las fechas
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        android.widget.TextView tvFechaInicio = new android.widget.TextView(this);
        tvFechaInicio.setText("📅 Fecha de inicio: (sin seleccionar)");
        tvFechaInicio.setPadding(0, 8, 0, 8);
        tvFechaInicio.setTextSize(14);

        android.widget.Button btnPickInicio = new android.widget.Button(this);
        btnPickInicio.setText("Elegir fecha de inicio");

        android.widget.TextView tvFechaFin = new android.widget.TextView(this);
        tvFechaFin.setText("📅 Fecha de devolución: (sin seleccionar)");
        tvFechaFin.setPadding(0, 8, 0, 8);
        tvFechaFin.setTextSize(14);

        android.widget.Button btnPickFin = new android.widget.Button(this);
        btnPickFin.setText("Elegir fecha de devolución");

        layout.addView(tvFechaInicio);
        layout.addView(btnPickInicio);
        layout.addView(tvFechaFin);
        layout.addView(btnPickFin);

        builder.setView(layout);
        builder.setTitle("Selecciona las fechas de renta");

        final String[] fechaInicioFinal = {null};
        final String[] fechaFinFinal = {null};

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int anio = cal.get(java.util.Calendar.YEAR);
        int mes = cal.get(java.util.Calendar.MONTH);
        int dia = cal.get(java.util.Calendar.DAY_OF_MONTH);

        btnPickInicio.setOnClickListener(v -> {
            new android.app.DatePickerDialog(this, (view, y, m, d) -> {
                fechaInicioFinal[0] = String.format("%04d-%02d-%02d", y, m + 1, d);
                tvFechaInicio.setText("📅 Fecha de inicio: " + fechaInicioFinal[0]);
            }, anio, mes, dia).show();
        });

        btnPickFin.setOnClickListener(v -> {
            new android.app.DatePickerDialog(this, (view, y, m, d) -> {
                fechaFinFinal[0] = String.format("%04d-%02d-%02d", y, m + 1, d);
                tvFechaFin.setText("📅 Fecha de devolución: " + fechaFinFinal[0]);
            }, anio, mes, dia).show();
        });

        builder.setPositiveButton("Enviar solicitud", (dialog, which) -> {
            if (fechaInicioFinal[0] == null || fechaFinFinal[0] == null) {
                Toast.makeText(this, "⚠️ Debes seleccionar ambas fechas", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmarEnvioSolicitud(miUsuarioId, fechaInicioFinal[0], fechaFinFinal[0]);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarEnvioSolicitud(int miUsuarioId, String fechaInicio, String fechaFin) {
        SolicitudRentaRequest solicitud = new SolicitudRentaRequest();
        solicitud.setIdUsArrendatario(miUsuarioId);
        solicitud.setIdUsArrendador(objetoActual.getIdUsArrendador());
        solicitud.setIdObjeto(objetoId);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaInicio(fechaInicio);
        solicitud.setFechaFin(fechaFin);
        solicitud.setMonto(objetoActual.getPrecio());

        Log.d(TAG, "📤 Enviando solicitud - Arrendatario: " + miUsuarioId +
                ", Arrendador: " + objetoActual.getIdUsArrendador() +
                ", Objeto: " + objetoId +
                ", Inicio: " + fechaInicio + ", Fin: " + fechaFin);

        apiService.crearSolicitudRenta(solicitud).enqueue(new Callback<SolicitudRenta>() {
            @Override
            public void onResponse(Call<SolicitudRenta> call, Response<SolicitudRenta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SolicitudRenta creada = response.body();
                    Log.d(TAG, "✅ Solicitud creada con ID: " + creada.getIdSolicitud());
                    Toast.makeText(Objetoo.this,
                            "✅ Solicitud enviada correctamente",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e(TAG, "❌ Error al crear solicitud: " + response.code());
                    Toast.makeText(Objetoo.this,
                            "❌ Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudRenta> call, Throwable t) {
                Log.e(TAG, "💥 Error de conexión: " + t.getMessage());
                Toast.makeText(Objetoo.this,
                        "💥 Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Método auxiliar para verificar disponibilidad
     * Maneja tanto "Disponible" como "true" como valores válidos
     */
    private boolean esDisponible(String estado) {
        if (estado == null) return false;

        String estadoLower = estado.trim().toLowerCase();
        return estadoLower.equals("disponible") || estadoLower.equals("true");
    }
}