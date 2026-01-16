package com.example.roy.solicitudes;

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

public class rentador extends Fragment {

    private ListView listView;
    private SolicitudesArrendadorAdapter adapter;
    private List<SolicitudRenta> solicitudes = new ArrayList<>();
    private ApiService apiService;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rentador, container, false);

        listView = view.findViewById(R.id.lvSolicitudesRentador);
        TextView emptyText = view.findViewById(R.id.empty_text);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        adapter = new SolicitudesArrendadorAdapter(requireContext(), solicitudes,
                new SolicitudesArrendadorAdapter.OnSolicitudActionListener() {
                    @Override
                    public void onAceptarClicked(SolicitudRenta solicitud) {
                        aprobarSolicitud(solicitud);
                    }

                    @Override
                    public void onRechazarClicked(SolicitudRenta solicitud) {
                        rechazarSolicitud(solicitud);
                    }
                });

        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);

        cargarSolicitudes();

        return view;
    }

    private void cargarSolicitudes() {
        if (currentUserId == -1) return;

        apiService.getSolicitudesArrendador(currentUserId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    solicitudes.clear();
                    solicitudes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aprobarSolicitud(SolicitudRenta solicitud) {
        apiService.aprobarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<SolicitudRenta>() {
            @Override
            public void onResponse(Call<SolicitudRenta> call, Response<SolicitudRenta> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud aprobada ✅", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error al aprobar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudRenta> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rechazarSolicitud(SolicitudRenta solicitud) {
        apiService.rechazarSolicitud(solicitud.getIdSolicitud()).enqueue(new Callback<SolicitudRenta>() {
            @Override
            public void onResponse(Call<SolicitudRenta> call, Response<SolicitudRenta> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error al rechazar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudRenta> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}