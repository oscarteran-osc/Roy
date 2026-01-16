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

import java.util.List;

public class RecomendadosAdapter extends RecyclerView.Adapter<RecomendadosAdapter.VH> {

    public interface OnItemClick {
        void onClick(com.example.roy.models.Objeto objeto);
    }

    private final List<com.example.roy.models.Objeto> list;
    private final OnItemClick listener;

    public RecomendadosAdapter(List<com.example.roy.models.Objeto> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recomendado, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        com.example.roy.models.Objeto o = list.get(position);

        h.titulo.setText(o.getNombre());
        h.precio.setText("$ " + o.getPrecio());

        String url = o.getImagenPrincipal();
        if (url != null) url = url.trim();

        if (url != null && !url.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.tent_image)
                    .error(R.drawable.tent_image)
                    .into(h.img);
        } else {
            h.img.setImageResource(R.drawable.tent_image);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(o));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView titulo, precio;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_img);
            titulo = itemView.findViewById(R.id.item_title);
            precio = itemView.findViewById(R.id.item_price);
        }
    }
}
