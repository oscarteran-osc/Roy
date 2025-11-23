package com.example.roy.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.roy.R;

import java.util.ArrayList;
import java.util.List;


public class resenas extends Fragment implements AdapterView.OnItemClickListener {
    String[] nombres = {"Monse Gutiérrez", "Abraham Aguilar"};
    String[] fechas = {"29/08/25", "13/04/25"};
    String[] descripciones = {"La tienda está en excelentes condiciones...", "Fácil de armar y resistente."};
    float[] calificaciones = {4.0f, 5.0f};
    int[] imagenes = {R.drawable.img,R.drawable.img_1};

    ListView listView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita;
        vistita = inflater.inflate(R.layout.fragment_resenas, container, false);
        listView = vistita.findViewById(R.id.rvResenas);
        listView.setOnItemClickListener(this);
        ResenaAdapter adaptadorcito = new ResenaAdapter(getContext(), nombres, fechas, descripciones,calificaciones, imagenes);
        listView.setAdapter(adaptadorcito);


        return vistita;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}