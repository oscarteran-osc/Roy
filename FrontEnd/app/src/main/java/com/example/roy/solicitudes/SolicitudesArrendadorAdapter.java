package com.example.roy.solicitudes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roy.R;
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesArrendadorAdapter extends BaseAdapter {

    public interface OnSolicitudActionListener {
        void onAceptarClicked(SolicitudRenta solicitud);
        void onRechazarClicked(SolicitudRenta solicitud);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private List<SolicitudRenta> solicitudes;
    private final OnSolicitudActionListener listener;

    public SolicitudesArrendadorAdapter(Context context, List<SolicitudRenta> solicitudes, OnSolicitudActionListener listener) {
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

        holder.tvNombreObjeto.setText("Objeto #" + solicitud.getIdObjeto());
        holder.tvSolicitante.setText("Solicitante: Usuario #" + solicitud.getIdUsArrendatario());
        holder.tvFechaSolicitud.setText("Fecha: " + solicitud.getFechaInicio());

        String estado = solicitud.getEstado() != null ? solicitud.getEstado().toUpperCase() : "PENDIENTE";
        holder.tvEstado.setText(estado);

        // Solo mostrar botones si está PENDIENTE
        if ("PENDIENTE".equals(estado)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);

            holder.btnAceptar.setOnClickListener(v -> {
                if (listener != null) listener.onAceptarClicked(solicitud);
            });

            holder.btnRechazar.setOnClickListener(v -> {
                if (listener != null) listener.onRechazarClicked(solicitud);
            });
        } else {
            holder.btnAceptar.setVisibility(View.GONE);
            holder.btnRechazar.setVisibility(View.GONE);
        }

        // Color del estado
        switch (estado) {
            case "APROBADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_green_dark));
                break;
            case "RECHAZADA":
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_red_dark));
                break;
            default:
                holder.tvEstado.setTextColor(context.getColor(android.R.color.holo_orange_dark));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivImagen;
        TextView tvNombreObjeto;
        TextView tvSolicitante;
        TextView tvFechaSolicitud;
        TextView tvEstado;
        Button btnAceptar;
        Button btnRechazar;

        ViewHolder(View view) {
            ivImagen = view.findViewById(R.id.imgObjeto);
            tvNombreObjeto = view.findViewById(R.id.tvNombreObjeto);
            tvSolicitante = view.findViewById(R.id.tvSolicitante);
            tvFechaSolicitud = view.findViewById(R.id.tvFechaSolicitud);
            tvEstado = view.findViewById(R.id.tvEstado);
            btnAceptar = view.findViewById(R.id.btnAceptar);
            btnRechazar = view.findViewById(R.id.btnRechazar);
        }
    }
}