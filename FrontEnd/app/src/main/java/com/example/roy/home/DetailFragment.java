package com.example.roy.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.roy.R;

public class DetailFragment extends Fragment {
    private static final String ARG_DETAIL = "detail";

    public static DetailFragment newInstance(String detail) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAIL, detail);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // En lugar de mostrar pantalla en blanco, redirigimos a la categoría correcta
        String detail = getArguments() != null ? getArguments().getString(ARG_DETAIL, "") : "";

        String categoria = mapearCategoria(detail);

        // Navegar al CategoryFragment correcto
        CategoryFragment categoryFragment = CategoryFragment.newInstance(categoria);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Retornar vista vacía mientras se hace la transición
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    private String mapearCategoria(String detail) {
        if (detail == null) return "Todo";

        String d = detail.toLowerCase().trim()
                .replace("á","a").replace("é","e").replace("í","i")
                .replace("ó","o").replace("ú","u");

        // "Tu próximo proyecto" → Tecnología
        if (d.contains("proyecto") || d.contains("tecnolog")) return "Tecnología";
        // "Escapada de fin de semana" → Eventos
        if (d.contains("escapada") || d.contains("semana") || d.contains("evento")) return "Eventos";
        // "Reparar algo sin gastar de más" → Herramientas
        if (d.contains("reparar") || d.contains("gastar") || d.contains("herramienta")) return "Herramientas";
        // "Ese evento especial" → Eventos
        if (d.contains("especial")) return "Eventos";

        return "Todo";
    }
}
