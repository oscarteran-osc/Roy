package com.example.roy.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

public class ObjetosCategoriaAdapter extends BaseAdapter {

    public interface OnItemActionListener {
        void onVerDetallesClicked(Objeto objeto);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private List<Objeto> objetos;
    private final OnItemActionListener listener;

    // 🔧 cambia el puerto si usas otro, pero en emulador normalmente es 10.0.2.2:8080
    private static final String BASE_URL = "http://10.0.2.2:8080";

    public ObjetosCategoriaAdapter(Context context,
                                   List<Objeto> objetos,
                                   OnItemActionListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.objetos = objetos != null ? objetos : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return objetos.size();
    }

    @Override
    public Object getItem(int position) {
        return objetos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return objetos.get(position).getIdObjeto();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_objeto_categoria, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Objeto objeto = objetos.get(position);

        holder.tvNombre.setText(objeto.getNombreObjeto());
        holder.tvCategoria.setText(objeto.getCategoria());
        holder.tvPrecio.setText("$" + String.format("%.2f", objeto.getPrecio()));

        // ✅ CARGAR IMAGEN REAL
        String url = objeto.getImagenUrl();

        if (url != null) {
            url = url.trim();
        }

        if (url != null && !url.isEmpty()) {
            // Si viene como "/uploads/xxx.jpg" o "uploads/xxx.jpg", lo hacemos completo
            if (url.startsWith("/")) {
                url = BASE_URL + url;
            } else if (!url.startsWith("http")) {
                url = BASE_URL + "/" + url;
            }

            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.camara)
                    .error(R.drawable.camara)
                    .into(holder.ivImagen);
        } else {

            com.bumptech.glide.Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.camara)
                    .error(R.drawable.camara)
                    .into(holder.ivImagen);

        }

        holder.btnDelete.setVisibility(View.GONE);
        holder.btnSolicitudes.setVisibility(View.GONE);

        View.OnClickListener detallesClick = v -> {
            if (listener != null) listener.onVerDetallesClicked(objeto);
        };

        holder.btnVerDetalles.setOnClickListener(detallesClick);
        holder.cardRoot.setOnClickListener(detallesClick);

        return convertView;
    }

    public void updateData(List<Objeto> nuevos) {
        this.objetos = nuevos != null ? nuevos : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        View cardRoot;
        ImageView ivImagen;
        ImageView btnDelete;
        TextView tvNombre;
        TextView tvCategoria;
        TextView tvPrecio;
        TextView btnVerDetalles;
        TextView btnSolicitudes;
        LinearLayout accionesContainer;

        ViewHolder(View view) {
            cardRoot         = view;
            ivImagen         = view.findViewById(R.id.imgObjeto);
            btnDelete        = view.findViewById(R.id.btnDelete);
            tvNombre         = view.findViewById(R.id.tvNombreObjeto);
            tvCategoria      = view.findViewById(R.id.tvCategoriaObjeto);
            tvPrecio         = view.findViewById(R.id.tvPrecioObjeto);
            btnVerDetalles   = view.findViewById(R.id.btnVerDetalles);
            btnSolicitudes   = view.findViewById(R.id.btnSolicitudes);
            accionesContainer= view.findViewById(R.id.accionesContainer);
        }
    }
}
