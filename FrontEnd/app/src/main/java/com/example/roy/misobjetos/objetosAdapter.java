package com.example.roy.misobjetos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roy.R;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

public class objetosAdapter extends BaseAdapter {

    public interface OnItemActionListener {
        void onVerDetallesClicked(Objeto objeto);
        void onVerSolicitudesClicked(Objeto objeto, View view);
        void onEliminarClicked(Objeto objeto, int position);
    }

    private Context context;
    private LayoutInflater inflater;
    private List<Objeto> objetos; // ✅ AHORA USA LA LISTA DE OBJETOS
    private OnItemActionListener listener;

    // ✅ NUEVO CONSTRUCTOR que recibe List<Objeto>
    public objetosAdapter(Context context, List<Objeto> objetos, OnItemActionListener listener) {
        this.context = context;
        this.objetos = objetos != null ? objetos : new ArrayList<>();
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.objeto, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // ✅ OBTENER EL OBJETO EN ESTA POSICIÓN
        Objeto objeto = objetos.get(position);

        // ✅ LLENAR LOS DATOS
        holder.tvNombre.setText(objeto.getNombreObjeto());
        holder.tvCategoria.setText(objeto.getCategoria());
        holder.tvPrecio.setText("$" + String.format("%.2f", objeto.getPrecio()));

        // TODO: Cargar imagen real con Glide cuando tengas URLs
        // Por ahora usa placeholder
        holder.ivImagen.setImageResource(R.drawable.camara);

        // ✅ LISTENERS
        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) listener.onVerDetallesClicked(objeto);
        });

        holder.btnSolicitudes.setOnClickListener(v -> {
            if (listener != null) listener.onVerSolicitudesClicked(objeto, v);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClicked(objeto, position);
        });

        return convertView;
    }

    // ✅ MÉTODO PARA ACTUALIZAR LA LISTA
    public void updateData(List<Objeto> nuevosObjetos) {
        this.objetos = nuevosObjetos != null ? nuevosObjetos : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder pattern para mejor performance
    static class ViewHolder {
        ImageView ivImagen;
        TextView tvNombre;
        TextView tvCategoria;
        TextView tvPrecio;
        Button btnDetalles;
        Button btnSolicitudes;
        ImageButton btnEliminar;

        ViewHolder(View view) {
            ivImagen = view.findViewById(R.id.imgobj);
            tvNombre = view.findViewById(R.id.nomobj);
            tvCategoria = view.findViewById(R.id.catobj);
            tvPrecio = view.findViewById(R.id.precioobj);
            btnDetalles = view.findViewById(R.id.btndetalles);
            btnSolicitudes = view.findViewById(R.id.btnsolis);
            btnEliminar = view.findViewById(R.id.delete);
        }
    }
}