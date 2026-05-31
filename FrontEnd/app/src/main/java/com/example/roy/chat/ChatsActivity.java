package com.example.roy.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.SolicitudRenta;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsActivity extends AppCompatActivity {

    private LinearLayout contenedor;
    private TextView tvSinChats;
    private ApiService apiService;
    private int userId;
    private int cargasPendientes = 2;
    private int totalAgregados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) { finish(); return; }

        apiService = RetrofitClient.getClient().create(ApiService.class);
        contenedor = findViewById(R.id.chats_contenedor);
        tvSinChats = findViewById(R.id.tvSinChats);
        findViewById(R.id.btnBackChats).setOnClickListener(v -> finish());

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        // Como arrendatario (rentador)
        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agregarItems(response.body(), "rentador");
                }
                verificarFin();
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) { verificarFin(); }
        });

        // Como arrendador (ofertante)
        apiService.getSolicitudesArrendador(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agregarItems(response.body(), "ofertante");
                }
                verificarFin();
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) { verificarFin(); }
        });
    }

    private void agregarItems(List<SolicitudRenta> lista, String rol) {
        runOnUiThread(() -> {
            for (SolicitudRenta s : lista) {
                int otroUsuario = "rentador".equals(rol) ? s.getIdUsArrendador() : s.getIdUsArrendatario();
                String otroNombre = "rentador".equals(rol)
                    ? (s.getNombreArrendador() != null ? s.getNombreArrendador() : "Arrendador")
                    : (s.getNombreArrendatario() != null ? s.getNombreArrendatario() : "Arrendatario");

                View item = getLayoutInflater().inflate(R.layout.item_chat_lista, contenedor, false);
                ((TextView) item.findViewById(R.id.tvChatNombre)).setText("Con: " + otroNombre);
                ((TextView) item.findViewById(R.id.tvChatObjeto)).setText(
                    s.getNombreObjeto() != null ? s.getNombreObjeto() : "Objeto");
                ((TextView) item.findViewById(R.id.tvChatEstado)).setText(
                    s.getEstado() != null ? s.getEstado() : "—");

                int finalOtroUsuario = otroUsuario;
                String finalOtroNombre = otroNombre;
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
                    intent.putExtra("idSolicitud", s.getIdSolicitud());
                    intent.putExtra("otroUsuario", finalOtroUsuario);
                    intent.putExtra("nombreOtro", finalOtroNombre);
                    startActivity(intent);
                });

                contenedor.addView(item);
                totalAgregados++;
            }
        });
    }

    private synchronized void verificarFin() {
        cargasPendientes--;
        if (cargasPendientes == 0) {
            runOnUiThread(() -> {
                if (totalAgregados == 0) tvSinChats.setVisibility(View.VISIBLE);
            });
        }
    }
}
