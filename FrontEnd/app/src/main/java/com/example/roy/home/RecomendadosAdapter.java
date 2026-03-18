package com.example.roy.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.List;

public class RecomendadosAdapter extends RecyclerView.Adapter<RecomendadosAdapter.ViewHolder> {

    private final List<Objeto> objetos;
    private final OnObjetoClickListener listener;

    // Interfaz para manejar clicks
    public interface OnObjetoClickListener {
        void onObjetoClick(Objeto objeto);
    }

    public RecomendadosAdapter(List<Objeto> objetos, OnObjetoClickListener listener) {
        this.objetos = objetos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecomendadosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recomendado, parent, false); // <-- tu layout item
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomendadosAdapter.ViewHolder holder, int position) {
        Objeto objeto = objetos.get(position);

        // Ejemplo de binding (ajusta a tu modelo / ids)
        holder.tvNombre.setText(objeto.getNombreObjeto());
        holder.tvPrecio.setText("$" + objeto.getPrecio()); // si tienes precio

        // Imagen (ajusta getUrlImagen o lo que uses)
        Glide.with(holder.itemView.getContext())
                .load(objeto.getImagenUrl()) // <-- ajusta esto
                .placeholder(R.drawable.ic_box_placeholder)
                .into(holder.ivFoto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onObjetoClick(objeto);
        });
    }

    @Override
    public int getItemCount() {
        return objetos != null ? objetos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre, tvPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.item_img);     // <-- ids de tu item xml
            tvNombre = itemView.findViewById(R.id.item_title);
            tvPrecio = itemView.findViewById(R.id.item_price);
        }
    }
}
