package com.example.roy.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Resena;
import com.example.roy.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resenas extends Fragment {

    private static final String TAG = "ResenasFragment";

    private LinearLayout containerResenas;
    private ApiService apiService;
    private SessionManager sessionManager;
    private int objetoId = -1;
    private LinearLayout layoutSinResenas;
    private FloatingActionButton fabAgregarResena;

    private int idUsReceptor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            objetoId = getArguments().getInt("objetoId", -1);
            idUsReceptor = getArguments().getInt("idUsReceptor", -1);
            Log.d(TAG, "onCreate - objetoId: " + objetoId + ", receptor: " + idUsReceptor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita = inflater.inflate(R.layout.fragment_resenas, container, false);

        containerResenas = vistita.findViewById(R.id.containerResenas);
        layoutSinResenas = vistita.findViewById(R.id.layoutSinResenas);
        fabAgregarResena = vistita.findViewById(R.id.fabAgregarResena);

        if (fabAgregarResena != null) {
            fabAgregarResena.setOnClickListener(v -> mostrarDialogoAgregarResena());
        }

        return vistita;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (objetoId != -1) {
            cargarResenas();
        } else {
            Toast.makeText(getContext(), "Error: ID de objeto no disponible",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "objetoId es -1");
        }
    }

    private void cargarResenas() {
        Log.d(TAG, "Cargando reseñas para objeto ID: " + objetoId);

        apiService.getResenasPorObjeto(objetoId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (!isAdded()) return;

                Log.d(TAG, "Respuesta recibida - código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Resena> resenas = response.body();
                    Log.d(TAG, "Reseñas cargadas: " + resenas.size());

                    if (resenas.isEmpty()) {
                        mostrarMensajeSinResenas();
                    } else {
                        ocultarMensajeSinResenas();
                        mostrarResenas(resenas);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar reseñas",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                    mostrarMensajeSinResenas();
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
                mostrarMensajeSinResenas();
            }
        });
    }

    private void mostrarResenas(List<Resena> resenas) {
        containerResenas.removeAllViews();

        for (Resena resena : resenas) {
            View resenaView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_resena, containerResenas, false);

            TextView tvNombre = resenaView.findViewById(R.id.tvNombreResena);
            TextView tvFecha = resenaView.findViewById(R.id.tvFechaResena);
            TextView tvComentario = resenaView.findViewById(R.id.tvComentarioResena);
            RatingBar ratingBar = resenaView.findViewById(R.id.ratingBarResena);

            tvNombre.setText(resena.getNombreAutor() != null ? resena.getNombreAutor() : "Usuario");
            tvFecha.setText(resena.getFechaResena() != null ? resena.getFechaResena() : "");
            tvComentario.setText(resena.getComentario());
            ratingBar.setRating(resena.getCalificacion());

            containerResenas.addView(resenaView);
        }
    }

    private void mostrarDialogoAgregarResena() {
        int userId = sessionManager.getUserId();

        Log.d(TAG, "Intentando agregar reseña - userId: " + userId);

        if (userId == -1) {
            Toast.makeText(getContext(), "Debes iniciar sesión para dejar una reseña",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == idUsReceptor) {
            Toast.makeText(getContext(), "No puedes dejar reseñas en tus propios objetos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

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

            enviarResena(userId, calificacion, comentario);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void enviarResena(int userId, float calificacion, String comentario) {
        if (objetoId == -1 || idUsReceptor == -1) {
            Toast.makeText(getContext(), "Error: faltan datos del objeto", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error - objetoId: " + objetoId + ", receptor: " + idUsReceptor);
            return;
        }

        Resena nuevaResena = new Resena();
        nuevaResena.setIdObjeto(objetoId);
        nuevaResena.setIdUsAutor(userId);
        nuevaResena.setIdUsReceptor(idUsReceptor);
        nuevaResena.setCalificacion((int) calificacion);
        nuevaResena.setComentario(comentario);

        Log.d(TAG, "Enviando reseña - objeto: " + objetoId + ", autor: " + userId +
                ", receptor: " + idUsReceptor + ", calificación: " + calificacion);

        apiService.crearResena(nuevaResena).enqueue(new Callback<Resena>() {
            @Override
            public void onResponse(Call<Resena> call, Response<Resena> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Reseña agregada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarResenas();
                } else {
                    Toast.makeText(getContext(), "Error al agregar reseña: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Resena> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void mostrarMensajeSinResenas() {
        if (layoutSinResenas != null) {
            layoutSinResenas.setVisibility(View.VISIBLE);
            containerResenas.setVisibility(View.GONE);
        }
    }

    private void ocultarMensajeSinResenas() {
        if (layoutSinResenas != null) {
            layoutSinResenas.setVisibility(View.GONE);
            containerResenas.setVisibility(View.VISIBLE);
        }
    }

    public void setObjetoId(int objetoId) {
        this.objetoId = objetoId;
        if (objetoId != -1) {
            cargarResenas();
        }
    }
}