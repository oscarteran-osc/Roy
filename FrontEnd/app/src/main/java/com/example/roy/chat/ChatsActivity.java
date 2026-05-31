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
import com.example.roy.models.Mensaje;
import com.example.roy.models.SolicitudRenta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsActivity extends AppCompatActivity {

    private LinearLayout contenedor;
    private TextView tvSinChats;
    private ApiService apiService;
    private int userId;
    private boolean cargando = false;

    private static class ChatItem {
        SolicitudRenta solicitud;
        String otroNombre;
        int otroUsuarioId;
        int noLeidos;
        long ultimaFecha;
        String ultimoMensajePreview;

        ChatItem(SolicitudRenta s, String nombre, int otroId) {
            this.solicitud = s;
            this.otroNombre = nombre;
            this.otroUsuarioId = otroId;
            this.noLeidos = 0;
            this.ultimaFecha = 0;
            this.ultimoMensajePreview = "";
        }
    }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!cargando) {
            contenedor.removeAllViews();
            tvSinChats.setVisibility(View.GONE);
            cargarChats();
        }
    }

    private void cargarChats() {
        cargando = true;
        List<ChatItem> items = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger pendientes = new AtomicInteger(2);

        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SolicitudRenta s : response.body()) {
                        String nombre = s.getNombreArrendador() != null ? s.getNombreArrendador() : "Arrendador";
                        items.add(new ChatItem(s, nombre, s.getIdUsArrendador()));
                    }
                }
                if (pendientes.decrementAndGet() == 0) cargarMensajes(items);
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (pendientes.decrementAndGet() == 0) cargarMensajes(items);
            }
        });

        apiService.getSolicitudesArrendador(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SolicitudRenta s : response.body()) {
                        String nombre = s.getNombreArrendatario() != null ? s.getNombreArrendatario() : "Arrendatario";
                        items.add(new ChatItem(s, nombre, s.getIdUsArrendatario()));
                    }
                }
                if (pendientes.decrementAndGet() == 0) cargarMensajes(items);
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (pendientes.decrementAndGet() == 0) cargarMensajes(items);
            }
        });
    }

    private void cargarMensajes(List<ChatItem> items) {
        if (items.isEmpty()) {
            cargando = false;
            runOnUiThread(() -> tvSinChats.setVisibility(View.VISIBLE));
            return;
        }

        AtomicInteger pendientes = new AtomicInteger(items.size());

        for (ChatItem item : items) {
            apiService.getMensajesPorSolicitud(item.solicitud.getIdSolicitud())
                .enqueue(new Callback<List<Mensaje>>() {
                    @Override
                    public void onResponse(Call<List<Mensaje>> call, Response<List<Mensaje>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (Mensaje m : response.body()) {
                                // No leídos dirigidos a mí
                                if (m.getIdDestinatario() != null
                                        && m.getIdDestinatario() == userId
                                        && !Boolean.TRUE.equals(m.getLeido())) {
                                    item.noLeidos++;
                                }
                                // Fecha más reciente
                                if (m.getFechaEnvio() != null) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                        Date fecha = sdf.parse(m.getFechaEnvio());
                                        if (fecha != null && fecha.getTime() > item.ultimaFecha) {
                                            item.ultimaFecha = fecha.getTime();
                                            item.ultimoMensajePreview = m.getContenido() != null ? m.getContenido() : "";
                                        }
                                    } catch (Exception ignored) {}
                                }
                            }
                        }
                        if (pendientes.decrementAndGet() == 0) mostrarChats(items);
                    }
                    @Override
                    public void onFailure(Call<List<Mensaje>> call, Throwable t) {
                        if (pendientes.decrementAndGet() == 0) mostrarChats(items);
                    }
                });
        }
    }

    private void mostrarChats(List<ChatItem> items) {
        Collections.sort(items, (a, b) -> {
            if (a.noLeidos > 0 && b.noLeidos == 0) return -1;
            if (a.noLeidos == 0 && b.noLeidos > 0) return 1;
            return Long.compare(b.ultimaFecha, a.ultimaFecha);
        });

        runOnUiThread(() -> {
            cargando = false;
            contenedor.removeAllViews();
            if (items.isEmpty()) {
                tvSinChats.setVisibility(View.VISIBLE);
                return;
            }
            for (ChatItem item : items) {
                View v = getLayoutInflater().inflate(R.layout.item_chat_lista, contenedor, false);
                ((TextView) v.findViewById(R.id.tvChatObjeto)).setText(
                    item.solicitud.getNombreObjeto() != null ? item.solicitud.getNombreObjeto() : "Objeto");
                ((TextView) v.findViewById(R.id.tvChatNombre)).setText("Con: " + item.otroNombre);

                TextView tvPreview = v.findViewById(R.id.tvUltimoMensaje);
                if (tvPreview != null && !item.ultimoMensajePreview.isEmpty()) {
                    tvPreview.setText(item.ultimoMensajePreview);
                    tvPreview.setVisibility(View.VISIBLE);
                }

                TextView badge = v.findViewById(R.id.tvBadgeNoLeidos);
                if (badge != null && item.noLeidos > 0) {
                    badge.setText(item.noLeidos > 9 ? "9+" : String.valueOf(item.noLeidos));
                    badge.setVisibility(View.VISIBLE);
                }

                v.setOnClickListener(click -> {
                    Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
                    intent.putExtra("idSolicitud", item.solicitud.getIdSolicitud());
                    intent.putExtra("otroUserId", item.otroUsuarioId);
                    intent.putExtra("nombreOtroUsuario", item.otroNombre);
                    startActivity(intent);
                });
                contenedor.addView(v);
            }
        });
    }
}
