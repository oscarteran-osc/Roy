package com.example.roy.misobjetos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roy.R;

public class objetosAdapter extends BaseAdapter {

    public interface OnItemActionListener {
        void onVerDetallesClicked(int position);
        void onVerSolicitudesClicked(int position, View view);
    }

    private Context context;
    private LayoutInflater inflater;
    public String[] nombres;
    public String[] categorias;
    public int[] precios;
    public int[] imagenes;
    private OnItemActionListener listener;

    public objetosAdapter(Context context, String[] nombres, String[] categorias, int[] precios, int[] imagenes, OnItemActionListener listener) {
        this.context = context;
        this.nombres = nombres;
        this.categorias = categorias;
        this.precios = precios;
        this.imagenes = imagenes;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // Si aún no asignaste datos, evita crash
        if (nombres == null) return 0;
        return nombres.length;
    }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.objeto, parent, false);
        }

        // Crea una variable final para usar en los listeners
        final View finalView = convertView;

        ImageView img = finalView.findViewById(R.id.imgobj);
        TextView nombre = finalView.findViewById(R.id.nomobj);
        TextView categoria = finalView.findViewById(R.id.catobj);
        TextView precio = finalView.findViewById(R.id.precioobj);
        Button btnDetalles = finalView.findViewById(R.id.btndetalles);
        Button btnSolis = finalView.findViewById(R.id.btnsolis);

        if (imagenes != null) img.setImageResource(imagenes[position]);
        if (nombres != null) nombre.setText(nombres[position]);
        if (categorias != null) categoria.setText(categorias[position]);
        if (precios != null) precio.setText("$" + precios[position]);

        btnDetalles.setOnClickListener(view -> {
            if (listener != null) listener.onVerDetallesClicked(position);
        });

        btnSolis.setOnClickListener(view -> {
            if (listener != null) listener.onVerSolicitudesClicked(position, finalView);
        });

        return finalView;
    }
}
