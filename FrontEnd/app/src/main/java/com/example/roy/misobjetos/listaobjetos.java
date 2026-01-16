package com.example.roy.misobjetos;

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
import com.example.roy.api.ApiService; // Importar ApiService
import com.example.roy.api.RetrofitClient; // Importar RetrofitClient
import com.example.roy.models.Objeto;

import java.util.ArrayList; // Necesario para inicializar la lista
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class listaobjetos extends Fragment {

    private ListView listView;
    private objetosAdapter adapter;
    private ApiService apiService; // ✅ Reemplaza a MockDataManager
    private int currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Obtener ID del usuario actual
        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1); // Usar -1 o un valor seguro si no está logueado

        // Verificar si el ID es válido
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_LONG).show();
            // TODO: Redirigir al login si el ID es -1
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listaobjetos, container, false);

        listView = view.findViewById(R.id.listaobjeto);

        // ✅ Inicializar Adaptador con lista vacía (los datos se cargarán asíncronamente)
        adapter = new objetosAdapter(
                getContext(),
                new ArrayList<>(), // Inicialmente vacía
                new objetosAdapter.OnItemActionListener() {
                    @Override
                    public void onVerDetallesClicked(Objeto objeto) {
                        // Lógica de navegación a detalles
                        Intent intent = new Intent(getContext(), detalles.class);
                        intent.putExtra("objetoId", objeto.getId());
                        // Pasa los demás datos si es necesario (o solo el ID para que la actividad de detalles los cargue)
                        startActivity(intent);
                    }

                    @Override
                    public void onVerSolicitudesClicked(Objeto objeto, View view) {
                        Toast.makeText(getContext(),
                                "Ver solicitudes de: " + objeto.getNombre(),
                                Toast.LENGTH_SHORT).show();
                        // TODO: Navegar a lista de solicitudes
                    }

                    @Override
                    public void onEliminarClicked(Objeto objeto, int position) {
                        mostrarDialogoEliminar(objeto, position);
                    }
                }
        );

        listView.setAdapter(adapter);
        return view;
    }

    // ✅ Reemplaza onResume para cargar datos al iniciar y volver
    @Override
    public void onResume() {
        super.onResume();
        cargarObjetos();
    }

    // =========================================================
    //                    MÉTODOS RETROFIT
    // =========================================================

    // ✅ MÉTODO PARA CARGAR LOS DATOS (Llamada GET)
    public void cargarObjetos() {
        if (currentUserId == -1) return;

        apiService.getObjetosPorUsuario(currentUserId).enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualizar el adaptador con los datos del servidor
                    adapter.updateData(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar objetos: " + response.code(), Toast.LENGTH_SHORT).show();
                    adapter.updateData(new ArrayList<>()); // Limpiar lista en caso de error
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al cargar objetos.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoEliminar(Objeto objeto, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar objeto")
                .setMessage("¿Estás seguro de eliminar '" + objeto.getNombre() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    realizarEliminacion(objeto.getId()); // Llamada DELETE
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ✅ MÉTODO PARA ELIMINAR OBJETO (Llamada DELETE)
    private void realizarEliminacion(int objetoId) {
        apiService.eliminarObjeto(objetoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Objeto eliminado", Toast.LENGTH_SHORT).show();
                    cargarObjetos(); // Recargar la lista para que el adaptador refleje el cambio
                } else {
                    Toast.makeText(getContext(), "Error al eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al eliminar objeto.", Toast.LENGTH_LONG).show();
            }
        });
    }
}