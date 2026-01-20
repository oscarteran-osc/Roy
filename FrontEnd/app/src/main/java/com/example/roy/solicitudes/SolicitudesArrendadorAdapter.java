package com.example.roy.solicitudes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roy.R;
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar solicitudes recibidas por el ARRENDADOR
 */
public class SolicitudesArrendadorAdapter extends BaseAdapter {

    public interface OnSolicitudArrendadorListener {
        void onAceptarClicked(SolicitudRenta solicitud);
        void onRechazarClicked(SolicitudRenta solicitud);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private List<SolicitudRenta> solicitudes;
    private final OnSolicitudArrendadorListener listener;

    public SolicitudesArrendadorAdapter(Context context, List<SolicitudRenta> solicitudes, OnSolicitudArrendadorListener listener) {
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
            convertView = inflater.inflate(R.layout.item_solicitud_arrendador, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SolicitudRenta solicitud = solicitudes.get(position);

        // Datos básicos
        holder.tvNombreObjeto.setText("Objeto #" + solicitud.getIdObjeto());
        holder.tvNombreArrendatario.setText("De: Usuario #" + solicitud.getIdUsArrendatario());

        String fechaInicio = solicitud.getFechaInicio();
        holder.tvFechaSolicitud.setText("Solicitud: " + (fechaInicio != null ? fechaInicio : "-"));

        // Estado
        String estado = solicitud.getEstado();
        if (estado == null) estado = "PENDIENTE";
        estado = estado.trim().toUpperCase();
        holder.tvEstado.setText(estado);

        // Configurar según estado
        switch (estado) {
            case "PENDIENTE":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_orange_dark));
                holder.layoutBotones.setVisibility(View.VISIBLE);
                holder.btnAceptar.setOnClickListener(v -> {
                    if (listener != null) listener.onAceptarClicked(solicitud);
                });
                holder.btnRechazar.setOnClickListener(v -> {
                    if (listener != null) listener.onRechazarClicked(solicitud);
                });
                break;

            case "APROBADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_green_dark));
                holder.layoutBotones.setVisibility(View.GONE);
                break;

            case "RECHAZADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_red_dark));
                holder.layoutBotones.setVisibility(View.GONE);
                break;

            default:
                holder.tvEstado.setTextColor(context.getColor(android.R.color.darker_gray));
                holder.layoutBotones.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    public void updateData(List<SolicitudRenta> nuevas) {
        this.solicitudes = nuevas != null ? nuevas : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView ivImagen;
        TextView tvNombreObjeto;
        TextView tvNombreArrendatario;
        TextView tvFechaSolicitud;
        TextView tvEstado;
        LinearLayout layoutBotones;
        Button btnAceptar;
        Button btnRechazar;

        ViewHolder(View view) {
            ivImagen = view.findViewById(R.id.imgObjeto);
            tvNombreObjeto = view.findViewById(R.id.tvNombreObjeto);
            tvNombreArrendatario = view.findViewById(R.id.tvNombreArrendatario);
            tvFechaSolicitud = view.findViewById(R.id.tvFechaSolicitud);
            tvEstado = view.findViewById(R.id.tvEstado);
            layoutBotones = view.findViewById(R.id.layoutBotones);
            btnAceptar = view.findViewById(R.id.btnAceptar);
            btnRechazar = view.findViewById(R.id.btnRechazar);
        }
    }
}