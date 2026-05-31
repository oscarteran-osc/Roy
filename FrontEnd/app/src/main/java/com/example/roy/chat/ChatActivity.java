package com.example.roy.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Mensaje;
import com.example.roy.models.MensajeRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private TextView tvTitulo;

    private ChatAdapter adapter;
    private List<Mensaje> mensajes = new ArrayList<>();

    private ApiService apiService;
    private int miUserId;
    private int otroUserId;
    private int idSolicitud;
    private String nombreOtroUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Obtener datos del Intent
        idSolicitud    = getIntent().getIntExtra("idSolicitud", -1);
        otroUserId     = getIntent().getIntExtra("otroUserId", -1);
        nombreOtroUsuario = getIntent().getStringExtra("nombreOtroUsuario");

        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        miUserId = prefs.getInt("userId", -1);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Vistas
        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje  = findViewById(R.id.etMensaje);
        btnEnviar  = findViewById(R.id.btnEnviar);
        tvTitulo   = findViewById(R.id.tvTitulo);

        tvTitulo.setText(nombreOtroUsuario != null ? nombreOtroUsuario : "Chat");

        // Adapter
        adapter = new ChatAdapter(mensajes, miUserId);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(adapter);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón enviar
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        // Cargar mensajes
        cargarMensajes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMensajes();
    }

    private void cargarMensajes() {
        if (idSolicitud != -1) {
            apiService.getMensajesPorSolicitud(idSolicitud).enqueue(new Callback<List<Mensaje>>() {
                @Override
                public void onResponse(Call<List<Mensaje>> call, Response<List<Mensaje>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mensajes.clear();
                        mensajes.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        if (!mensajes.isEmpty())
                            rvMensajes.scrollToPosition(mensajes.size() - 1);
                    }
                }
                @Override
                public void onFailure(Call<List<Mensaje>> call, Throwable t) {}
            });
        }
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty() || miUserId == -1 || otroUserId == -1) return;

        MensajeRequest request = new MensajeRequest();
        request.setIdDestinatario(otroUserId);
        request.setIdSolicitud(idSolicitud != -1 ? idSolicitud : null);
        request.setContenido(texto);

        apiService.enviarMensaje(miUserId, request).enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etMensaje.setText("");
                    mensajes.add(response.body());
                    adapter.notifyItemInserted(mensajes.size() - 1);
                    rvMensajes.scrollToPosition(mensajes.size() - 1);
                }
            }
            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {}
        });
    }
}
