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

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.models.Resena;

import java.util.ArrayList;
import java.util.List;

public class ResenaAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Resena> resenas;

    public ResenaAdapter(Context context, List<Resena> resenas) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.resenas = resenas != null ? resenas : new ArrayList<>();
    }

    @Override
    public int getCount() {
        return resenas.size();
    }

    @Override
    public Object getItem(int position) {
        return resenas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return resenas.get(position).getIdResena();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_resena, parent, false);
            holder = new ViewHolder();

            // ✅ IDs actualizados
            holder.txtNombre = convertView.findViewById(R.id.tvNombreResena);
            holder.txtFecha = convertView.findViewById(R.id.tvFechaResena);
            holder.txtComentario = convertView.findViewById(R.id.tvComentarioResena);
            holder.rating = convertView.findViewById(R.id.ratingBarResena);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Resena resena = resenas.get(position);

        // Nombre del autor
        holder.txtNombre.setText(
                resena.getNombreAutor() != null ? resena.getNombreAutor() : "Usuario"
        );

        // Fecha
        holder.txtFecha.setText(
                resena.getFechaResena() != null ? resena.getFechaResena() : ""
        );

        // Comentario
        holder.txtComentario.setText(
                resena.getComentario() != null ? resena.getComentario() : ""
        );

        // Rating
        holder.rating.setRating(
                resena.getCalificacion() != null ? resena.getCalificacion() : 0
        );

        // Forzar estrellas amarillas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList yellow = ColorStateList.valueOf(Color.parseColor("#FFC107"));
            holder.rating.setProgressTintList(yellow);
            holder.rating.setSecondaryProgressTintList(yellow);
        }

        return convertView;
    }

    public void actualizarResenas(List<Resena> nuevasResenas) {
        this.resenas = nuevasResenas != null ? nuevasResenas : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView avatar;
        TextView txtNombre;
        TextView txtFecha;
        TextView txtComentario;
        RatingBar rating;
    }
}