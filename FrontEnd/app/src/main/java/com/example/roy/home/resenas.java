package com.example.roy.home;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Resena;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resenas extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ResenasFragment";

    private ListView listView;
    private ResenaAdapter adapter;
    private ApiService apiService;
    private int objetoId = -1;
    private LinearLayout layoutSinResenas;
    private FloatingActionButton fabAgregarResena;

    private int idUsReceptor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (getArguments() != null) {
            objetoId = getArguments().getInt("objetoId", -1);
            idUsReceptor = getArguments().getInt("idUsReceptor", -1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita = inflater.inflate(R.layout.fragment_resenas, container, false);

        listView = vistita.findViewById(R.id.rvResenas);
        layoutSinResenas = vistita.findViewById(R.id.layoutSinResenas);
        fabAgregarResena = vistita.findViewById(R.id.fabAgregarResena);

        listView.setOnItemClickListener(this);

        // Inicializar adaptador con lista vacía
        adapter = new ResenaAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        // Configurar botón flotante para agregar reseña
        if (fabAgregarResena != null) {
            fabAgregarResena.setOnClickListener(v -> mostrarDialogoAgregarResena());
        }

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
        Log.d(TAG, "Cargando reseñas para objeto ID: " + objetoId);

        apiService.getResenasPorObjeto(objetoId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Resena> resenas = response.body();
                    Log.d(TAG, "Reseñas cargadas: " + resenas.size());

                    if (resenas.isEmpty()) {
                        mostrarMensajeSinResenas();
                    } else {
                        ocultarMensajeSinResenas();
                        adapter.actualizarResenas(resenas);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar reseñas",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void mostrarDialogoAgregarResena() {
        // Verificar que el usuario esté autenticado
        SharedPreferences prefs = requireContext().getSharedPreferences("ROY_PREFS", 0);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Debes iniciar sesión para dejar una reseña",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear diálogo personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_resena, null);

        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        EditText editComentario = dialogView.findViewById(R.id.dialogEditComentario);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviar);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnEnviar.setOnClickListener(v -> {
            float calificacion = ratingBar.getRating();
            String comentario = editComentario.getText().toString().trim();

            if (calificacion == 0) {
                Toast.makeText(getContext(), "Por favor selecciona una calificación",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (comentario.isEmpty()) {
                Toast.makeText(getContext(), "Por favor escribe un comentario",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar reseña
            enviarResena(userId, calificacion, comentario);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void enviarResena(int userId, float calificacion, String comentario) {

        if (objetoId == -1 || idUsReceptor == -1) {
            Toast.makeText(getContext(), "Error: faltan datos del objeto", Toast.LENGTH_SHORT).show();
            return;
        }

        Resena nuevaResena = new Resena();
        nuevaResena.setIdObjeto(objetoId);
        nuevaResena.setIdUsAutor(userId);
        nuevaResena.setIdUsReceptor(idUsReceptor);
        nuevaResena.setCalificacion((int) calificacion);
        nuevaResena.setComentario(comentario);

        apiService.crearResena(nuevaResena).enqueue(new Callback<Resena>() {
            @Override
            public void onResponse(Call<Resena> call, Response<Resena> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Reseña agregada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarResenas();
                } else {
                    Toast.makeText(getContext(), "Error al agregar reseña", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Resena> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }


    private void mostrarMensajeSinResenas() {
        if (layoutSinResenas != null) {
            layoutSinResenas.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void ocultarMensajeSinResenas() {
        if (layoutSinResenas != null) {
            layoutSinResenas.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Opcional: agregar funcionalidad al hacer clic en una reseña
        // Por ejemplo, mostrar opciones para reportar
    }

    // Método público para que el fragmento padre pase el objetoId
    public void setObjetoId(int objetoId) {
        this.objetoId = objetoId;
        if (adapter != null && objetoId != -1) {
            cargarResenas();
        }
    }
}