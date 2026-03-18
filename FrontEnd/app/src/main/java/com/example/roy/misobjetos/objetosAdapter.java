package com.example.roy.misobjetos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.models.Objeto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class objetosAdapter extends BaseAdapter {

    private Context context;
    private List<Objeto> objetos;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onVerDetallesClicked(Objeto objeto);
        void onEliminarClicked(Objeto objeto, int position);
    }

    public objetosAdapter(Context context, List<Objeto> objetos, OnItemActionListener listener) {
        this.context = context;
        this.objetos = objetos;
        this.listener = listener;
    }

    public void updateData(List<Objeto> nuevosObjetos) {
        this.objetos = nuevosObjetos;
        notifyDataSetChanged();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_objeto, parent, false);
            holder = new ViewHolder();

            holder.imgobj = convertView.findViewById(R.id.imgobj);
            holder.nomobj = convertView.findViewById(R.id.nomobj);
            holder.catChip = convertView.findViewById(R.id.catChip);
            holder.precioobj = convertView.findViewById(R.id.precioobj);
            holder.btndetalles = convertView.findViewById(R.id.btndetalles);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Objeto objeto = objetos.get(position);

        // Nombre
        holder.nomobj.setText(objeto.getNombreObjeto());

        // Categoría
        String categoria = objeto.getCategoria() != null ? objeto.getCategoria() : "Sin categoría";
        holder.catChip.setText(categoria);

        // Precio
        double precio = objeto.getPrecio();
        holder.precioobj.setText(String.format("$%.0f / día", precio));

        // Imagen principal
        String imagenUrl = objeto.getImagenUrl();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.casacampania)
                    .error(R.drawable.casacampania)
                    .centerCrop()
                    .into(holder.imgobj);
        } else {
            holder.imgobj.setImageResource(R.drawable.casacampania);
        }

        // Click en Detalles
        holder.btndetalles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetallesClicked(objeto);
            }
        });

        // ELIMINADO: Click en Eliminar ya que no existe el botón en el layout

        return convertView;
    }

    static class ViewHolder {
        ImageView imgobj;
        TextView nomobj;
        Chip catChip;
        TextView precioobj;
        MaterialButton btndetalles;
        // ELIMINADO: ImageButton btnEliminar;
    }
}