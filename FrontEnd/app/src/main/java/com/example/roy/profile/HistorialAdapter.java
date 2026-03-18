package com.example.roy.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<PerfilFragment.ItemHistorial> items;
    private PerfilFragment.OnHistorialClickListener listener;

    public HistorialAdapter(List<PerfilFragment.ItemHistorial> items,
                            PerfilFragment.OnHistorialClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_objeto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PerfilFragment.ItemHistorial item = items.get(position);

        holder.tvNombre.setText(item.getNombre());
        holder.tvPrecio.setText(item.getPrecio());

        Glide.with(holder.itemView.getContext())
                .load(item.getImagenUrl())
                .placeholder(R.drawable.ic_box_placeholder)
                .error(R.drawable.ic_box_placeholder)
                .centerCrop()
                .into(holder.imgObjeto);

        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView imgObjeto;
        TextView tvNombre;
        TextView tvPrecio;

        ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.cardHistorial);
            imgObjeto = view.findViewById(R.id.imgObjetoHistorial);
            tvNombre = view.findViewById(R.id.tvNombreObjeto);
            tvPrecio = view.findViewById(R.id.tvPrecioObjeto);
        }
    }
}