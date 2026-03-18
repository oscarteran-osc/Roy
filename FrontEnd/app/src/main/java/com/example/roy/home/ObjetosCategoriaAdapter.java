package com.example.roy.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

public class    ObjetosCategoriaAdapter extends BaseAdapter {

    public interface OnObjetoClickListener {
        void onClick(Objeto objeto);
    }

    private final Context context;
    private final LayoutInflater inflater;

    private final List<Objeto> lista = new ArrayList<>();
    @Nullable private final OnObjetoClickListener listener;

    public ObjetosCategoriaAdapter(@NonNull Context context, @Nullable List<Objeto> lista) {
        this(context, lista, null);
    }

    public ObjetosCategoriaAdapter(@NonNull Context context,
                                   @Nullable List<Objeto> lista,
                                   @Nullable OnObjetoClickListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        if (lista != null) this.lista.addAll(lista);
    }

    public void updateData(@Nullable List<Objeto> nuevaLista) {
        lista.clear();
        if (nuevaLista != null) lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Objeto getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        Integer id = lista.get(position).getIdObjeto();
        return (id != null) ? id : position;
    }

    static class ViewHolder {
        ImageView imgObjeto;
        TextView tvNombre, tvPrecio, tvExtra;
        RatingBar ratingBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder h;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_objeto_categoria, parent, false);

            h = new ViewHolder();
            h.imgObjeto  = convertView.findViewById(R.id.imgObjeto);
            h.tvNombre   = convertView.findViewById(R.id.tvNombre);
            h.tvPrecio   = convertView.findViewById(R.id.tvPrecio);
            h.tvExtra    = convertView.findViewById(R.id.tvExtra);
            h.ratingBar  = convertView.findViewById(R.id.ratingBar);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        Objeto o = getItem(position);

        // null-safety
        String nombre = (o.getNombreObjeto() != null && !o.getNombreObjeto().trim().isEmpty())
                ? o.getNombreObjeto()
                : "Objeto";

        String estado = Boolean.parseBoolean(o.getEstado()) ? "Disponible" : "No disponible";

        Double precioObj = o.getPrecio(); // en tu CategoryFragment lo estás tratando como Double
        double precio = (precioObj != null) ? precioObj : 0.0;

        if (h.tvNombre != null) h.tvNombre.setText(nombre);
        if (h.tvPrecio != null) h.tvPrecio.setText(String.format("$%.0f / día", precio));

        if (h.ratingBar != null) {
            h.ratingBar.setStepSize(0.1f);
            h.ratingBar.setRating(generarRatingPorPrecio(precio));
        }

        String entrega = (precio <= 120) ? "Entrega hoy" : "Entrega 24h";
        if (h.tvExtra != null) h.tvExtra.setText(entrega + " • " + estado);

        // Glide
        String url = o.getImagenUrl();

        if (h.imgObjeto != null) {
            if (url == null || url.trim().isEmpty()) {
                h.imgObjeto.setImageResource(R.drawable.banner_image);
            } else {
                Glide.with(context)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.banner_image)
                        .error(R.drawable.banner_image)
                        .into(h.imgObjeto);
            }
        }

        if (listener != null) {
            convertView.setOnClickListener(v -> listener.onClick(o));
        } else {
            convertView.setOnClickListener(null);
        }

        return convertView;
    }

    private float generarRatingPorPrecio(double precio) {
        if (precio <= 60)  return 4.9f;
        if (precio <= 100) return 4.7f;
        if (precio <= 160) return 4.5f;
        if (precio <= 230) return 4.3f;
        return 4.1f;
    }
}
