package com.example.roy.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.List;
import java.util.Locale;

public class ObjetosArrendadorAdapter extends RecyclerView.Adapter<ObjetosArrendadorAdapter.ObjetoViewHolder> {

    private List<Objeto> objetos;
    private OnObjetoClickListener listener;

    public interface OnObjetoClickListener {
        void onObjetoClick(Objeto objeto);
    }

    public ObjetosArrendadorAdapter(List<Objeto> objetos, OnObjetoClickListener listener) {
        this.objetos = objetos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ObjetoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_objeto_arrendador, parent, false);
        return new ObjetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjetoViewHolder holder, int position) {
        Objeto objeto = objetos.get(position);
        holder.bind(objeto, listener);
    }

    @Override
    public int getItemCount() {
        return objetos != null ? objetos.size() : 0;
    }

    static class ObjetoViewHolder extends RecyclerView.ViewHolder {

        private TextView tvObjNombre;
        private TextView tvObjCategoria;
        private TextView tvObjPrecio;

        public ObjetoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObjNombre = itemView.findViewById(R.id.tvObjNombre);
            tvObjCategoria = itemView.findViewById(R.id.tvObjCategoria);
            tvObjPrecio = itemView.findViewById(R.id.tvObjPrecio);
        }

        public void bind(Objeto objeto, OnObjetoClickListener listener) {
            // Nombre del objeto
            tvObjNombre.setText(objeto.getNombreObjeto() != null ?
                    objeto.getNombreObjeto() : "Sin nombre");

            // Categoría
            tvObjCategoria.setText(objeto.getCategoria() != null ?
                    objeto.getCategoria() : "Sin categoría");

            // Precio
            tvObjPrecio.setText(String.format(Locale.US, "$%.0f/día", objeto.getPrecio()));

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onObjetoClick(objeto);
                }
            });
        }
    }
}