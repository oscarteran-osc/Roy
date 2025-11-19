package com.example.roy.misobjetos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.roy.R;

public class listaobjetos extends Fragment implements AdapterView.OnItemClickListener {
    String[] nombres = {"Canon EOS M50 marca 2 Cámara", "Taladro"};

    String[] categorias = {"Tecnología", "Herramienta"};

    int[] precios = {500, 300};
    int[] imagenes = {R.drawable.camara, R.drawable.taladro};
    ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita;
        vistita = inflater.inflate(R.layout.fragment_listaobjetos, container, false);
        listView = vistita.findViewById(R.id.listaobjeto);
        objetosAdapter adaptadorcito = new objetosAdapter(
                getContext(),
                nombres,
                categorias,
                precios,
                imagenes,
                new objetosAdapter.OnItemActionListener() {
                    @Override
                    public void onVerDetallesClicked(int position) {
                        Intent intentito = new Intent(getContext(), detalles.class);
                        startActivity(intentito);
                    }

                    @Override
                    public void onVerSolicitudesClicked(int position, View view) {
                        Navigation.findNavController(view).navigate(R.id.detalles);
                    }
                }
        );
        listView.setAdapter(adaptadorcito);

        return vistita;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}