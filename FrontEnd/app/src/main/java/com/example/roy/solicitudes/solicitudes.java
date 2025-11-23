package com.example.roy.solicitudes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.roy.R;

public class solicitudes extends Fragment {

    private Button btnRentador;
    private Button btnOfertante;

    // Almacena el fragmento activo actualmente para la lógica de doble clic
    private String currentFragmentTag = "RENTADOR_TAG";

    public solicitudes() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // IMPORTANTE: este layout debe ser igual o muy parecido a activity_main
        // pero como fragment, por ejemplo: fragment_main_tabs.xml
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRentador = view.findViewById(R.id.btn_rentador);
        btnOfertante = view.findViewById(R.id.btn_ofertante);

        // 1. Cargar el Fragmento inicial (Rentador)
        if (savedInstanceState == null) {
            loadChildFragment(new Rentador(), "RENTADOR_TAG");
            updateButtonState(btnRentador, btnOfertante);
        }

        // 2. Listeners de los botones
        btnRentador.setOnClickListener(v -> {
            handleTabClick(new Rentador(), "RENTADOR_TAG", btnRentador, btnOfertante);
        });

        btnOfertante.setOnClickListener(v -> {
            handleTabClick(new Ofertante(), "OFERTANTE_TAG", btnOfertante, btnRentador);
        });
    }

    // ==========================================================
    // MÉTODOS DE UTILIDAD (versión Fragment)
    // ==========================================================

    /**
     * Carga o reemplaza el Fragment hijo en el contenedor.
     */
    private void loadChildFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contenedor_frag, fragment, tag) // contenedor dentro del layout del fragment
                .commit();
        currentFragmentTag = tag;
    }

    /**
     * Actualiza el estado visual de los botones.
     */
    private void updateButtonState(Button selectedButton, Button unselectedButton) {
        selectedButton.setSelected(true);
        unselectedButton.setSelected(false);
        // Si usas backgrounds distintos:
        // selectedButton.setBackgroundResource(R.drawable.bg_selected);
        // unselectedButton.setBackgroundResource(R.drawable.bg_unselected);
    }

    /**
     * Maneja la lógica de clic y previene la recarga si ya está activo.
     */
    private void handleTabClick(Fragment fragmentToLoad,
                                String tag,
                                Button clickedButton,
                                Button otherButton) {

        if (!currentFragmentTag.equals(tag)) {
            loadChildFragment(fragmentToLoad, tag);
            updateButtonState(clickedButton, otherButton);
        }
    }
}
