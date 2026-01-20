package com.example.roy.solicitudes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolicitudesArrendatarioFragment extends Fragment
        implements SolicitudesArrendatarioAdapter.OnSolicitudActionListener {

    private ListView listView;
    private TextView emptyText;
    private SolicitudesArrendatarioAdapter adapter;
    private List<SolicitudRenta> solicitudes;
    private ApiService apiService;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listSolicitudes);

        // Intenta ambos nombres de SharedPreferences por compatibilidad
        prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // Si no lo encuentra, intenta con "RoyPrefs"
        if (userId == -1) {
            prefs = requireContext().getSharedPreferences("RoyPrefs", Context.MODE_PRIVATE);
        }

        apiService = RetrofitClient.getClient().create(ApiService.class);

        solicitudes = new ArrayList<>();
        adapter = new SolicitudesArrendatarioAdapter(requireContext(), solicitudes, this);
        listView.setAdapter(adapter);

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        int userId = prefs.getInt("userId", -1);

        Log.d("SOLICITUDES_DEBUG", "🔍 Buscando solicitudes para userId: " + userId);

        if (userId == -1) {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (!isAdded()) return;

                // AGREGA ESTOS LOGS
                Log.d("SOLICITUDES_DEBUG", "📡 Response code: " + response.code());
                Log.d("SOLICITUDES_DEBUG", "📦 Body is null: " + (response.body() == null));

                if (response.body() != null) {
                    Log.d("SOLICITUDES_DEBUG", "📊 Cantidad de solicitudes: " + response.body().size());
                }

                if (response.isSuccessful() && response.body() != null) {
                    solicitudes.clear();
                    solicitudes.addAll(response.body());
                    adapter.notifyDataSetChanged();


                } else if (response.code() == 404) {
                    solicitudes.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "⚠️ No tienes solicitudes aún", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "❌ Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("SOLICITUDES_DEBUG", "💥 Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "💥 Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPagarClicked(SolicitudRenta solicitud) {
        // ✅ Primero obtener el objeto para sacar su precio
        apiService.getObjetoPorId(solicitud.getIdObjeto()).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Objeto objeto = response.body();
                    double monto = objeto.getPrecio();

                    Intent intent = new Intent(getContext(), PayPalPaymentActivity.class);
                    intent.putExtra("ID_SOLICITUD", solicitud.getIdSolicitud());
                    intent.putExtra("MONTO", monto);
                    intent.putExtra("nombreObjeto", objeto.getNombreObjeto());

                    Log.d("PAGO_DEBUG", "🚀 Abriendo PayPal - ID: " + solicitud.getIdSolicitud() + ", MONTO: " + monto);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos del objeto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelarClicked(SolicitudRenta solicitud) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar solicitud")
                .setMessage("¿Estás seguro de cancelar esta solicitud?")
                .setPositiveButton("Sí", (dialog, which) -> cancelarSolicitud(solicitud))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onEliminarClicked(SolicitudRenta solicitud) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar solicitud")
                .setMessage("¿Deseas eliminar esta solicitud rechazada?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarSolicitud(solicitud))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cancelarSolicitud(SolicitudRenta solicitud) {
        apiService.cancelarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud cancelada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "No se pudo cancelar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarSolicitud(SolicitudRenta solicitud) {
        apiService.eliminarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud eliminada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "No se pudo eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}