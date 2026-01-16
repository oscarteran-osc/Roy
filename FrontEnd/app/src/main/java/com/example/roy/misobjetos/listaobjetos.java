package com.example.roy.misobjetos;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

        adapter = new objetosAdapter(requireContext(), new ArrayList<>(),
                new objetosAdapter.OnItemActionListener() {
                    @Override
                    public void onVerDetallesClicked(Objeto objeto) {
                        Intent intent = new Intent(getContext(), detalles.class);
                        intent.putExtra("objetoId", objeto.getIdObjeto());
                        startActivity(intent);
                    }

                    @Override
                    public void onVerSolicitudesClicked(Objeto objeto, View view) {
                        Toast.makeText(getContext(),
                                "Solicitudes de: " + objeto.getNombreObjeto(),
                                Toast.LENGTH_SHORT).show();
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
        apiService.getObjetos().enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoEliminar(Objeto objeto) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar objeto")
                .setMessage("¿Eliminar \"" + objeto.getNombreObjeto() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> eliminarObjeto(objeto.getIdObjeto()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarObjeto(int id) {
        apiService.eliminarObjeto(id).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) { cargarObjetos(); }
            @Override public void onFailure(Call<Void> c, Throwable t) {}
        });
    }
}
