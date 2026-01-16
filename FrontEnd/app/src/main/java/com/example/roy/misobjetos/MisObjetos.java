package com.example.roy.misobjetos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;

public class MisObjetos extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mis_objetos, container, false);

        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorobjetos, new listaobjetos())
                    .commit();
        }

        view.findViewById(R.id.btnAgregarObjeto).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), plantillaagregar.class));
        });

        return view;
    }
}
