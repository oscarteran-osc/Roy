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

    // Clase interna para ordenar chats
    private static class ChatItem {
        SolicitudRenta solicitud;
        String rol;
        String otroNombre;
        int otroUsuarioId;
        int noLeidos;
        long ultimaFecha; // millis para ordenar
        String ultimoMensajePreview;

        ChatItem(SolicitudRenta s, String rol, String nombre, int otroId) {
            this.solicitud = s;
            this.rol = rol;
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

        cargarChats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        contenedor.removeAllViews();
        tvSinChats.setVisibility(View.GONE);
        cargarChats();
    }

    private void cargarChats() {
        List<ChatItem> items = new ArrayList<>();
        AtomicInteger cargasPendientes = new AtomicInteger(2);

        // Como arrendatario
        apiService.getSolicitudesArrendatario(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SolicitudRenta s : response.body()) {
                        String nombre = s.getNombreArrendador() != null ? s.getNombreArrendador() : "Arrendador";
                        items.add(new ChatItem(s, "rentador", nombre, s.getIdUsArrendador()));
                    }
                }
                if (cargasPendientes.decrementAndGet() == 0)
                    cargarMensajesParaItems(items);
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (cargasPendientes.decrementAndGet() == 0)
                    cargarMensajesParaItems(items);
            }
        });

        // Como arrendador
        apiService.getSolicitudesArrendador(userId).enqueue(new Callback<List<SolicitudRenta>>() {
            @Override
            public void onResponse(Call<List<SolicitudRenta>> call, Response<List<SolicitudRenta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SolicitudRenta s : response.body()) {
                        String nombre = s.getNombreArrendatario() != null ? s.getNombreArrendatario() : "Arrendatario";
                        items.add(new ChatItem(s, "ofertante", nombre, s.getIdUsArrendatario()));
                    }
                }
                if (cargasPendientes.decrementAndGet() == 0)
                    cargarMensajesParaItems(items);
            }
            @Override
            public void onFailure(Call<List<SolicitudRenta>> call, Throwable t) {
                if (cargasPendientes.decrementAndGet() == 0)
                    cargarMensajesParaItems(items);
            }
        });
    }

    private void cargarMensajesParaItems(List<ChatItem> items) {
        if (items.isEmpty()) {
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
                            List<Mensaje> mensajes = response.body();
                            int noLeidos = 0;
                            long ultimaFecha = 0;
                            String preview = "";

                            for (Mensaje m : mensajes) {
                                // Contar no leídos dirigidos a mí
                                if (m.getIdDestinatario() == userId && !m.isLeido()) {
                                    noLeidos++;
                                }
                                // Fecha del último mensaje
                                if (m.getFechaEnvio() != null) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                        Date fecha = sdf.parse(m.getFechaEnvio());
                                        if (fecha != null && fecha.getTime() > ultimaFecha) {
                                            ultimaFecha = fecha.getTime();
                                            preview = m.getContenido();
                                        }
                                    } catch (Exception ignored) {}
                                }
                            }
                            item.noLeidos = noLeidos;
                            item.ultimaFecha = ultimaFecha;
                            item.ultimoMensajePreview = preview;
                        }

                        if (pendientes.decrementAndGet() == 0) {
                            mostrarChatsOrdenados(items);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Mensaje>> call, Throwable t) {
                        if (pendientes.decrementAndGet() == 0) {
                            mostrarChatsOrdenados(items);
                        }
                    }
                });
        }
    }

    private void mostrarChatsOrdenados(List<ChatItem> items) {
        // Ordenar: primero los que tienen no leídos, luego por fecha más reciente
        Collections.sort(items, (a, b) -> {
            if (a.noLeidos > 0 && b.noLeidos == 0) return -1;
            if (a.noLeidos == 0 && b.noLeidos > 0) return 1;
            return Long.compare(b.ultimaFecha, a.ultimaFecha);
        });

        runOnUiThread(() -> {
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

                // Preview último mensaje
                TextView tvPreview = v.findViewById(R.id.tvUltimoMensaje);
                if (!item.ultimoMensajePreview.isEmpty()) {
                    tvPreview.setText(item.ultimoMensajePreview);
                    tvPreview.setVisibility(View.VISIBLE);
                }

                // Badge de no leídos
                TextView badge = v.findViewById(R.id.tvBadgeNoLeidos);
                if (item.noLeidos > 0) {
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
