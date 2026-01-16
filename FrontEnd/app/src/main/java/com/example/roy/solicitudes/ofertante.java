package com.example.roy.solicitudes;

import android.content.Intent;
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

import static android.content.Context.MODE_PRIVATE;

public class ofertante extends Fragment implements SolicitudesArrendatarioAdapter.OnSolicitudActionListener {

    private ListView listView;
    private TextView emptyText;
    private SolicitudesArrendatarioAdapter adapter;
    private List<SolicitudRenta> solicitudes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ofertante, container, false);

        listView = view.findViewById(R.id.lvSolicitudesOfertante);
        emptyText = view.findViewById(R.id.empty_text);

        solicitudes = new ArrayList<>();
        adapter = new SolicitudesArrendatarioAdapter(getContext(), solicitudes, this);
        listView.setAdapter(adapter);

        cargarSolicitudes();

        return view;
    }

    private void cargarSolicitudes() {
        // ✅ USAR EL MISMO NOMBRE: "RoyPrefs"
        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            emptyText.setText("Error: No se pudo identificar al usuario");
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    solicitudes.clear();
                    solicitudes.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (solicitudes.isEmpty()) {
                        emptyText.setText("No tienes solicitudes");
                        emptyText.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        emptyText.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar: " + response.code(), Toast.LENGTH_SHORT).show();
                    emptyText.setText("Error al cargar solicitudes");
                    emptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                emptyText.setText("Error de conexión");
                emptyText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPagarClicked(SolicitudRenta solicitud) {
        // Ir a la pantalla de método de pago
        Intent intent = new Intent(getActivity(), metododepago.class);
        intent.putExtra("idSolicitud", solicitud.getIdSolicitud());
        intent.putExtra("monto", solicitud.getMonto());
        intent.putExtra("nombreObjeto", "Objeto #" + solicitud.getIdObjeto());
        startActivity(intent);
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
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.eliminarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud cancelada", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cancelar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarSolicitud(SolicitudRenta solicitud) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.eliminarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error al eliminar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}