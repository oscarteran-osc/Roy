package com.example.roy.solicitudes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class SolicitudesArrendatarioFragment extends Fragment
        implements SolicitudesArrendatarioAdapter.OnSolicitudActionListener {

    private ListView listView;
    private SolicitudesArrendatarioAdapter adapter;
    private List<SolicitudRenta> lista;

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


        prefs = requireContext().getSharedPreferences("RoyPrefs", Context.MODE_PRIVATE);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        lista = new ArrayList<>();
        adapter = new SolicitudesArrendatarioAdapter(requireContext(), lista, this);
        listView.setAdapter(adapter);

        cargarSolicitudesReales();
    }

    private void cargarSolicitudesReales() {
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "No hay sesión activa.", Toast.LENGTH_LONG).show();
            return;
        }

        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 404) {
                    // tu backend manda 404 si está vacío
                    lista.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "No tienes solicitudes aún.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // ==================================================
    // ACCIONES DEL ADAPTER
    // ==================================================

    @Override
    public void onPagarClicked(SolicitudRenta solicitud) {
        // Por ahora manda a tu activity de PayPal
        // Ideal: que tu SolicitudRenta traiga "monto" real
        Intent intent = new Intent(getContext(), PayPalPaymentActivity.class);
        intent.putExtra("idSolicitud", solicitud.getIdSolicitud());

        // Si no tienes monto en solicitud, deja uno temporal
        intent.putExtra("monto", 500.00);

        intent.putExtra("nombreObjeto", "Objeto #" + solicitud.getIdObjeto());
        startActivity(intent);
    }

    @Override
    public void onCancelarClicked(SolicitudRenta solicitud) {
        apiService.eliminarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud cancelada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudesReales();
                } else {
                    Toast.makeText(getContext(), "No se pudo cancelar: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onEliminarClicked(SolicitudRenta solicitud) {
        apiService.eliminarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud eliminada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudesReales(); // refresca
                } else {
                    Toast.makeText(getContext(), "No se pudo eliminar: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

