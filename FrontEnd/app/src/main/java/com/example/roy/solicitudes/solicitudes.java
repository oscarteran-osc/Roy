package com.example.roy.solicitudes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.google.android.material.button.MaterialButton;

public class solicitudes extends Fragment {

    private MaterialButton btnRentador, btnOfertante;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudes, container, false);

        btnRentador = view.findViewById(R.id.btn_rentador);
        btnOfertante = view.findViewById(R.id.btn_ofertante);

        // Cargar rentador por defecto
        cargarFragment(new rentador());
        btnRentador.setChecked(true);

        btnRentador.setOnClickListener(v -> {
            btnRentador.setChecked(true);
            btnOfertante.setChecked(false);
            cargarFragment(new rentador());
        });

        btnOfertante.setOnClickListener(v -> {
            btnOfertante.setChecked(true);
            btnRentador.setChecked(false);
            cargarFragment(new ofertante());
        });

        return view;
    }

    private void cargarFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_frag, fragment)
                .commit();
    }
}