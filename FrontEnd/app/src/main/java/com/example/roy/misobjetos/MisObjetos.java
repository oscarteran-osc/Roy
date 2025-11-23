package com.example.roy.misobjetos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.roy.R;

public class MisObjetos extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mis_objetos, container, false);

        Button agregadito = view.findViewById(R.id.agregarobj);
        agregadito.setOnClickListener(this);

        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorobjetos, new listaobjetos())
                    .commit();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.agregarobj) {
            Intent intent = new Intent(getContext(), plantillaagregar.class);
            startActivity(intent);
        }
    }
}

