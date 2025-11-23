package com.example.roy.solicitudes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roy.R;

public class Ofertante extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout específico para las solicitudes de Rentador (tu XML de cards)
        return inflater.inflate(R.layout.fragment_ofertante, container, false);
        // Asegúrate de crear el archivo XML: 'fragment_rentador_layout.xml'
    }
}