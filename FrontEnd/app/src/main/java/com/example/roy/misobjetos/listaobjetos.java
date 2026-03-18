package com.example.roy.misobjetos;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class listaobjetos extends Fragment {

    private ListView listView;
    private TextView tvMensajeVacio;
    private objetosAdapter adapter;
    private ApiService apiService;
    private int currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listaobjetos, container, false);
        listView = view.findViewById(R.id.listaobjeto);
        //tvMensajeVacio = view.findViewById(R.id.tvMensajeVacio);

        adapter = new objetosAdapter(requireContext(), new ArrayList<>(),
                new objetosAdapter.OnItemActionListener() {
                    @Override
                    public void onVerDetallesClicked(Objeto objeto) {
                        Intent intent = new Intent(getContext(), detalles.class);
                        intent.putExtra("objetoId", objeto.getIdObjeto());
                        startActivity(intent);
                    }

                    @Override
                    public void onEliminarClicked(Objeto objeto, int position) {
                        mostrarDialogoEliminar(objeto);
                    }
                });

        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarObjetos();
    }

    private void cargarObjetos() {
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            mostrarMensajeVacio(true);
            return;
        }

        apiService.getObjetos().enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filtrar solo los objetos del usuario actual
                    List<Objeto> todosLosObjetos = response.body();
                    List<Objeto> misObjetos = new ArrayList<>();

                    for (Objeto objeto : todosLosObjetos) {
                        if (objeto.getIdUsArrendador() == currentUserId) {
                            misObjetos.add(objeto);
                        }
                    }

                    adapter.updateData(misObjetos);

                    // Mostrar u ocultar mensaje vacío
                    mostrarMensajeVacio(misObjetos.isEmpty());
                } else {
                    Toast.makeText(getContext(), "Error al cargar objetos", Toast.LENGTH_SHORT).show();
                    mostrarMensajeVacio(true);
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                mostrarMensajeVacio(true);
            }
        });
    }

    private void mostrarMensajeVacio(boolean mostrar) {
        if (tvMensajeVacio != null && listView != null) {
            if (mostrar) {
                tvMensajeVacio.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                tvMensajeVacio.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void mostrarDialogoEliminar(Objeto objeto) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar objeto")
                .setMessage("¿Estás seguro de eliminar \"" + objeto.getNombreObjeto() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> eliminarObjeto(objeto.getIdObjeto()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarObjeto(int id) {
        apiService.eliminarObjeto(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful()) {
                    Toast.makeText(getContext(), "Objeto eliminado correctamente", Toast.LENGTH_SHORT).show();
                    cargarObjetos();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar objeto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> c, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}