package com.example.roy.solicitudes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roy.R;
import com.example.roy.home.Objetoo;
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

public class SolicitudesArrendatarioAdapter extends BaseAdapter {

    // Interface para los callbacks
    public interface OnSolicitudActionListener {
        void onPagarClicked(SolicitudRenta solicitud);
        void onCancelarClicked(SolicitudRenta solicitud);
        void onEliminarClicked(SolicitudRenta solicitud);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private List<SolicitudRenta> solicitudes;
    private final OnSolicitudActionListener listener;

    public SolicitudesArrendatarioAdapter(Context context, List<SolicitudRenta> solicitudes, OnSolicitudActionListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.solicitudes = solicitudes != null ? solicitudes : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return solicitudes.size();
    }

    @Override
    public Object getItem(int position) {
        return solicitudes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return solicitudes.get(position).getIdSolicitud();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_solicitud_arrendatario, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SolicitudRenta solicitud = solicitudes.get(position);

        // ✅ CLICK EN NOMBRE/IMAGEN DEL OBJETO → Abrir página del objeto
        View.OnClickListener abrirObjeto = v -> {
            Intent intent = new Intent(context, Objetoo.class);
            intent.putExtra("objetoId", solicitud.getIdObjeto());
            context.startActivity(intent);
        };

        // ✅ Mostrar nombre real del objeto
        String nombreObjeto = solicitud.getNombreObjeto();
        if (nombreObjeto != null && !nombreObjeto.isEmpty()) {
            holder.tvNombreObjeto.setText(nombreObjeto);
        } else {
            holder.tvNombreObjeto.setText("Objeto #" + solicitud.getIdObjeto());
        }
        holder.tvNombreObjeto.setOnClickListener(abrirObjeto);

        // ✅ Mostrar nombre real del arrendador
        String nombreArrendador = solicitud.getNombreArrendador();
        if (nombreArrendador != null && !nombreArrendador.isEmpty()) {
            holder.tvNombreArrendador.setText("De: " + nombreArrendador);
        } else {
            holder.tvNombreArrendador.setText("De: Usuario #" + solicitud.getIdUsArrendador());
        }

        // ✅ Cargar imagen del objeto
        String imagenUrl = solicitud.getImagenObjeto();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .centerCrop()
                    .into(holder.ivImagen);
        } else {
            holder.ivImagen.setImageResource(R.drawable.error);
        }
        holder.ivImagen.setOnClickListener(abrirObjeto);

        String fechaInicio = solicitud.getFechaInicio();
        holder.tvFechaSolicitud.setText("Fecha: " + (fechaInicio != null ? fechaInicio : "-"));

        String fechaFin = solicitud.getFechaFin();
        holder.tvFechaRespuesta.setText("Respuesta: " + (fechaFin != null ? fechaFin : "-"));

        // Estado (protegido contra null)
        String estado = solicitud.getEstado();
        if (estado == null) estado = "PENDIENTE";
        estado = estado.trim().toUpperCase();

        holder.tvEstado.setText(estado);

        // ✅ Configurar botones según estado
        switch (estado) {
            case "APROBADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_green_dark));
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Pagar");
                holder.btnAccion.setBackgroundTintList(context.getColorStateList(R.color.blue_primary));
                holder.btnAccion.setOnClickListener(v -> {
                    if (listener != null) listener.onPagarClicked(solicitud);
                });
                break;

            case "PENDIENTE":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_orange_dark));
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Cancelar");
                holder.btnAccion.setBackgroundTintList(context.getColorStateList(R.color.blue_primary));
                holder.btnAccion.setOnClickListener(v -> {
                    if (listener != null) listener.onCancelarClicked(solicitud);
                });
                break;

            case "RECHAZADA":
            case "CANCELADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_red_dark));
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Borrar");
                holder.btnAccion.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
                holder.btnAccion.setOnClickListener(v -> {
                    if (listener != null) listener.onEliminarClicked(solicitud);
                });
                break;

            case "PAGADA":
            case "COMPLETADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_blue_dark));
                holder.btnAccion.setVisibility(View.VISIBLE);
                holder.btnAccion.setText("Borrar");
                holder.btnAccion.setBackgroundTintList(context.getColorStateList(android.R.color.darker_gray));
                holder.btnAccion.setOnClickListener(v -> {
                    if (listener != null) listener.onEliminarClicked(solicitud);
                });
                break;

            default:
                holder.tvEstado.setTextColor(context.getColor(android.R.color.darker_gray));
                holder.btnAccion.setVisibility(View.GONE);
                holder.btnAccion.setOnClickListener(null);
                break;
        }

        return convertView;
    }

    public void updateData(List<SolicitudRenta> nuevas) {
        this.solicitudes = nuevas != null ? nuevas : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder para optimizar rendimiento
    static class ViewHolder {
        ImageView ivImagen;
        TextView tvNombreObjeto;
        TextView tvNombreArrendador;
        TextView tvFechaSolicitud;
        TextView tvEstado;
        TextView tvFechaRespuesta;
        Button btnAccion;

        ViewHolder(View view) {
            ivImagen = view.findViewById(R.id.imgObjeto);
            tvNombreObjeto = view.findViewById(R.id.tvNombreObjeto);
            tvNombreArrendador = view.findViewById(R.id.tvNombreArrendador);
            tvFechaSolicitud = view.findViewById(R.id.tvFechaSolicitud);
            tvEstado = view.findViewById(R.id.tvEstado);
            tvFechaRespuesta = view.findViewById(R.id.tvFechaRespuesta);
            btnAccion = view.findViewById(R.id.btnAccion);
        }
    }
}