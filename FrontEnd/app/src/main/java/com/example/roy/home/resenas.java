package com.example.roy.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Resena;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resenas extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ResenaAdapter adapter;
    private ApiService apiService;
    private int objetoId = -1;
    private TextView tvSinResenas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar API service
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Obtener el ID del objeto desde los argumentos del fragmento padre
        if (getArguments() != null) {
            objetoId = getArguments().getInt("objetoId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita = inflater.inflate(R.layout.fragment_resenas, container, false);

        listView = vistita.findViewById(R.id.rvResenas);
        listView.setOnItemClickListener(this);

        // Inicializar adaptador con lista vacía
        adapter = new ResenaAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        return vistita;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Intentar obtener el objetoId del fragmento padre si no lo tenemos
        if (objetoId == -1 && getParentFragment() != null) {
            Bundle parentArgs = getParentFragment().getArguments();
            if (parentArgs != null) {
                objetoId = parentArgs.getInt("objetoId", -1);
            }
        }

        // Cargar reseñas
        if (objetoId != -1) {
            cargarResenas();
        } else {
            Toast.makeText(getContext(), "Error: ID de objeto no disponible",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarResenas() {
        apiService.getResenasPorObjeto(objetoId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Resena> resenas = response.body();

                    if (resenas.isEmpty()) {
                        mostrarMensajeSinResenas();
                    } else {
                        adapter.actualizarResenas(resenas);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar reseñas",
                            Toast.LENGTH_SHORT).show();
                    Log.e("resenas", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("resenas", "Error: ", t);
            }
        });
    }

    private void mostrarMensajeSinResenas() {
        // Puedes crear un TextView para mostrar "No hay reseñas aún"
        Toast.makeText(getContext(), "No hay reseñas para este objeto",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Aquí puedes agregar funcionalidad cuando se hace clic en una reseña
        // Por ejemplo, mostrar opciones para reportar o ver más detalles
    }

    // Método público para que el fragmento padre pase el objetoId
    public void setObjetoId(int objetoId) {
        this.objetoId = objetoId;
        if (adapter != null && objetoId != -1) {
            cargarResenas();
        }
    }
}