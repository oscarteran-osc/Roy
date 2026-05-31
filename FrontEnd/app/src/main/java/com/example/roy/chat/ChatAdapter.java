package com.example.roy.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roy.R;
import com.example.roy.models.Mensaje;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MensajeVH> {

    private final List<Mensaje> mensajes;
    private final int miUserId;

    public ChatAdapter(List<Mensaje> mensajes, int miUserId) {
        this.mensajes = mensajes;
        this.miUserId = miUserId;
    }

    @NonNull
    @Override
    public MensajeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new MensajeVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeVH holder, int position) {
        Mensaje m = mensajes.get(position);
        boolean esMio = m.getIdRemitente() != null && m.getIdRemitente() == miUserId;

        holder.tvContenido.setText(m.getContenido());

        // Alinear burbuja según si es mío o del otro
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                holder.tvContenido.getLayoutParams();

        if (esMio) {
            holder.tvContenido.setBackgroundResource(R.drawable.bg_mensaje_propio);
            holder.container.setGravity(Gravity.END);
        } else {
            holder.tvContenido.setBackgroundResource(R.drawable.bg_mensaje_otro);
            holder.container.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class MensajeVH extends RecyclerView.ViewHolder {
        TextView tvContenido;
        LinearLayout container;

        MensajeVH(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            container   = itemView.findViewById(R.id.containerMensaje);
        }
    }
}
