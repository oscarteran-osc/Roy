package com.example.roy.solicitudes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.roy.R;
import com.google.android.material.button.MaterialButton;

/**
 * Fragment principal que maneja las dos vistas de solicitudes:
 * - Rentador: Solicitudes que YO envié
 * - Ofertante: Solicitudes que ME enviaron
 */
public class solicitudes extends Fragment {

    private MaterialButton btnRentador;
    private MaterialButton btnOfertante;
    private String currentFragmentTag = "RENTADOR_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRentador = view.findViewById(R.id.btn_rentador);
        btnOfertante = view.findViewById(R.id.btn_ofertante);

        // Cargar fragmento inicial (Rentador - Solicitudes enviadas)
        if (savedInstanceState == null) {
            loadChildFragment(new SolicitudesArrendatarioFragment(), "RENTADOR_TAG");
            updateButtonState(btnRentador, btnOfertante);
        }

        // Listeners de botones
        btnRentador.setOnClickListener(v ->
                handleTabClick(new SolicitudesArrendatarioFragment(), "RENTADOR_TAG", btnRentador, btnOfertante)
        );

        btnOfertante.setOnClickListener(v ->
                handleTabClick(new SolicitudesArrendadorFragment(), "OFERTANTE_TAG", btnOfertante, btnRentador)
        );
    }

    /**
     * Carga un fragment hijo en el contenedor
     */
    private void loadChildFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contenedor_frag, fragment, tag)
                .commit();
        currentFragmentTag = tag;
    }

    /**
     * Actualiza el estado visual de los botones
     */
    private void updateButtonState(MaterialButton selectedButton, MaterialButton unselectedButton) {
        selectedButton.setSelected(true);
        unselectedButton.setSelected(false);
    }

    /**
     * Maneja el clic en pestañas y previene recargas innecesarias
     */
    private void handleTabClick(Fragment fragmentToLoad, String tag,
                                MaterialButton clickedButton, MaterialButton otherButton) {
        if (!currentFragmentTag.equals(tag)) {
            loadChildFragment(fragmentToLoad, tag);
            updateButtonState(clickedButton, otherButton);
        }
    }
}