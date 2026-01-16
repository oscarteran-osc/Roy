package com.example.roy.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.List;

public class ObjetosArrendadorAdapter extends RecyclerView.Adapter<ObjetosArrendadorAdapter.VH> {

    public interface OnObjetoClick {
        void onClick(Objeto objeto);
    }

    private List<Objeto> data;
    private final OnObjetoClick listener;

    public ObjetosArrendadorAdapter(List<Objeto> data, OnObjetoClick listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<Objeto> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_objeto_arrendador, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Objeto obj = data.get(position);

        //holder.tvNombre.setText(obj.getNombreObjeto());
        holder.tvCategoria.setText(obj.getCategoria());
        holder.tvPrecio.setText(obj.getPrecio() + ""); // ajusta formato si es double

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(obj);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvPrecio;
        VH(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvObjNombre);
            tvCategoria = itemView.findViewById(R.id.tvObjCategoria);
            tvPrecio = itemView.findViewById(R.id.tvObjPrecio);
        }
    }
}
