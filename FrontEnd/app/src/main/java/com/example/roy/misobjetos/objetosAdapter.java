package com.example.roy.misobjetos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private List<Objeto> objetos;
    private OnItemActionListener listener;

    public objetosAdapter(Context context, List<Objeto> objetos, OnItemActionListener listener) {
        this.context = context;
        this.objetos = objetos != null ? objetos : new ArrayList<>();
        this.listener = listener;
    }

    @Override public int getCount() { return objetos.size(); }
    @Override public Object getItem(int pos) { return objetos.get(pos); }
    @Override public long getItemId(int pos) { return objetos.get(pos).getIdObjeto(); }

    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        if (v == null) v = LayoutInflater.from(context).inflate(R.layout.objeto, parent, false);

        Objeto obj = objetos.get(pos);

        ((TextView) v.findViewById(R.id.nomobj)).setText(obj.getNombreObjeto());
        ((TextView) v.findViewById(R.id.catobj)).setText(obj.getCategoria());
        ((TextView) v.findViewById(R.id.precioobj)).setText("$" + obj.getPrecio());

       // ((ImageButton) v.findViewById(R.id.delete))
         //       .setOnClickListener(b -> listener.onEliminarClicked(obj, pos));

        v.findViewById(R.id.btndetalles)
                .setOnClickListener(b -> listener.onVerDetallesClicked(obj));

        v.findViewById(R.id.btnsolis)
                .setOnClickListener(b -> listener.onVerSolicitudesClicked(obj, b));

        return v;
    }

    public void updateData(List<Objeto> nuevos) {
        objetos = nuevos;
        notifyDataSetChanged();
    }
}
