package com.example.roy.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.roy.R;

public class ResenaAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    // 🔹 Arreglos que TÚ vas a llenar desde la Activity
    public String[] nombres;
    public String[] fechas;
    public String[] comentarios;
    public float[] estrellas;
    public int[] avatares;

    public ResenaAdapter(Context context, String[] nombres, String[] fechas, String[] comentarios, float[] estrellas, int[] avatares) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.nombres = nombres;
        this.fechas = fechas;
        this.comentarios = comentarios;
        this.estrellas = estrellas;
        this.avatares = avatares;
    }

    @Override
    public int getCount() {
        if (nombres == null) return 0;
        return nombres.length;
    }

    @Override
    public Object getItem(int position) {
        return null; // no usamos objeto como tal
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.item_resena, parent, false);
        }

        // 🔹 Referencias al layout
        ImageView avatar = v.findViewById(R.id.imgAvatar);
        TextView txtNombre = v.findViewById(R.id.tvNombre);
        TextView txtFecha = v.findViewById(R.id.tvFecha);
        TextView txtComentario = v.findViewById(R.id.tvContenido);
        RatingBar rating = v.findViewById(R.id.rating);

        // 🔹 Setear datos (asumiendo que todos los arreglos tienen el mismo tamaño)
        if (avatares != null)   avatar.setImageResource(avatares[position]);
        if (nombres != null)    txtNombre.setText(nombres[position]);
        if (fechas != null)     txtFecha.setText(fechas[position]);
        if (comentarios != null)txtComentario.setText(comentarios[position]);
        if (estrellas != null)  rating.setRating(estrellas[position]);

        // 🔹 Forzar estrellas amarillas (opcional, como en tu RecyclerView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList yellow = ColorStateList.valueOf(Color.parseColor("#FFC107"));
            rating.setProgressTintList(yellow);
            rating.setSecondaryProgressTintList(yellow);
            rating.setIndeterminateTintList(yellow);
        }

        return v;
    }
}
