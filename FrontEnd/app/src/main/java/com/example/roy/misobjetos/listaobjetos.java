package com.example.roy.misobjetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import static android.content.Context.MODE_PRIVATE;

public class listaobjetos extends Fragment {

    private static final String TAG = "ListaObjetos";
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

        Log.d(TAG, "onCreate - userId obtenido: " + currentUserId);

        if (currentUserId == -1) {
            Log.e(TAG, "ERROR: Usuario no autenticado (userId = -1)");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listaobjetos, container, false);

        listView = view.findViewById(R.id.listaobjeto);

        adapter = new objetosAdapter(
                getContext(),
                new ArrayList<>(),
                new objetosAdapter.OnItemActionListener() {
                    @Override
                    public void onVerDetallesClicked(Objeto objeto) {
                        Intent intent = new Intent(getContext(), detalles.class);
                        intent.putExtra("objetoId", objeto.getIdObjeto());
                        startActivity(intent);
                    }

                    @Override
                    public void onVerSolicitudesClicked(Objeto objeto, View view) {
                        if (isAdded()) {
                            Toast.makeText(getContext(),
                                    "Ver solicitudes de: " + objeto.getNombreObjeto(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onEliminarClicked(Objeto objeto, int position) {
                        mostrarDialogoEliminar(objeto, position);
                    }
                }
        );

        listView.setAdapter(adapter);
        Log.d(TAG, "onCreateView - Adapter establecido");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Iniciando carga de objetos");
        cargarObjetos();
    }

    public void cargarObjetos() {
        // ✅ Validación de contexto y userId
        if (!isAdded()) {
            Log.w(TAG, "Fragment no está adjunto, no se puede cargar objetos");
            return;
        }

        if (currentUserId == -1) {
            Log.e(TAG, "cargarObjetos - No se puede cargar: userId inválido");
            Toast.makeText(getContext(), "Error: Inicia sesión para ver tus objetos", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "cargarObjetos - Llamando API para userId: " + currentUserId);

        apiService.getObjetosPorUsuario(currentUserId).enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                // ✅ VERIFICAR QUE EL FRAGMENT SIGA ADJUNTO antes de hacer cualquier cosa
                if (!isAdded()) {
                    Log.w(TAG, "Fragment ya no está adjunto, ignorando respuesta");
                    return;
                }

                Log.d(TAG, "onResponse - Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Objeto> objetos = response.body();
                    Log.d(TAG, "Objetos recibidos: " + objetos.size());

                    for (int i = 0; i < objetos.size(); i++) {
                        Objeto obj = objetos.get(i);
                        Log.d(TAG, "Objeto " + i + ": " + obj.getNombreObjeto() + " - $" + obj.getPrecio());
                    }

                    adapter.updateData(objetos);

                    if (objetos.isEmpty()) {
                        Toast.makeText(getContext(), "No tienes objetos registrados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code() + " - " + response.message());

                    // ✅ Mensaje específico para 404
                    if (response.code() == 404) {
                        Log.e(TAG, "ENDPOINT NO ENCONTRADO - Verifica tu ApiService");
                        Toast.makeText(getContext(),
                                "Error: El servidor no encontró el endpoint. Contacta al desarrollador.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(),
                                "Error al cargar objetos: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }

                    adapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                // ✅ VERIFICAR QUE EL FRAGMENT SIGA ADJUNTO
                if (!isAdded()) {
                    Log.w(TAG, "Fragment ya no está adjunto, ignorando error");
                    return;
                }

                Log.e(TAG, "onFailure - Error de red", t);
                Log.e(TAG, "Mensaje: " + t.getMessage());
                Log.e(TAG, "Clase: " + t.getClass().getName());

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                adapter.updateData(new ArrayList<>());
            }
        });
    }

    private void mostrarDialogoEliminar(Objeto objeto, int position) {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar objeto")
                .setMessage("¿Estás seguro de eliminar '" + objeto.getNombreObjeto() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    realizarEliminacion(objeto.getIdObjeto());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarEliminacion(int objetoId) {
        if (!isAdded()) return;

        Log.d(TAG, "Eliminando objeto con ID: " + objetoId);

        apiService.eliminarObjeto(objetoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Log.d(TAG, "Objeto eliminado exitosamente");
                    Toast.makeText(getContext(), "Objeto eliminado", Toast.LENGTH_SHORT).show();
                    cargarObjetos();
                } else {
                    Log.e(TAG, "Error al eliminar: " + response.code());
                    Toast.makeText(getContext(), "Error al eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;

                Log.e(TAG, "Error de red al eliminar", t);
                Toast.makeText(getContext(), "Error de conexión al eliminar", Toast.LENGTH_LONG).show();
            }
        });
    }
}