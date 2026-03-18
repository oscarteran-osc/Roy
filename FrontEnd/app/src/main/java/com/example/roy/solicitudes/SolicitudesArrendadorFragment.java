package com.example.roy.solicitudes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment para el ARRENDADOR (Ofertante)
 * Muestra solicitudes que OTROS usuarios le enviaron para rentar SUS objetos
 */
public class SolicitudesArrendadorFragment extends Fragment
        implements SolicitudesArrendadorAdapter.OnSolicitudArrendadorListener {

    private ListView listView;
    private TextView emptyText;
    private SolicitudesArrendadorAdapter adapter;
    private List<SolicitudRenta> solicitudes;
    private ApiService apiService;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes_arrendador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewSolicitudes);
        emptyText = view.findViewById(R.id.emptyText);

        // Intenta ambos nombres de SharedPreferences por compatibilidad
        prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // Si no lo encuentra, intenta con "RoyPrefs"
        if (userId == -1) {
            prefs = requireContext().getSharedPreferences("RoyPrefs", Context.MODE_PRIVATE);
        }

        apiService = RetrofitClient.getClient().create(ApiService.class);

        solicitudes = new ArrayList<>();
        adapter = new SolicitudesArrendadorAdapter(requireContext(), solicitudes, this);
        listView.setAdapter(adapter);

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Endpoint diferente: solicitudes que LE ENVIARON al usuario
        apiService.getSolicitudesArrendador(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    solicitudes.clear();
                    solicitudes.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (solicitudes.isEmpty()) {
                        listView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        listView.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.GONE);
                    }
                } else if (response.code() == 404) {
                    solicitudes.clear();
                    adapter.notifyDataSetChanged();
                    listView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAceptarClicked(SolicitudRenta solicitud) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Aprobar solicitud")
                .setMessage("¿Deseas aprobar esta solicitud de renta?")
                .setPositiveButton("Aprobar", (dialog, which) -> aprobarSolicitud(solicitud))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onRechazarClicked(SolicitudRenta solicitud) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Rechazar solicitud")
                .setMessage("¿Estás seguro de rechazar esta solicitud?")
                .setPositiveButton("Rechazar", (dialog, which) -> rechazarSolicitud(solicitud))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aprobarSolicitud(SolicitudRenta solicitud) {
        apiService.aprobarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud aprobada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "No se pudo aprobar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rechazarSolicitud(SolicitudRenta solicitud) {
        apiService.rechazarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "No se pudo rechazar: " + response.code(), Toast.LENGTH_SHORT).show();
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