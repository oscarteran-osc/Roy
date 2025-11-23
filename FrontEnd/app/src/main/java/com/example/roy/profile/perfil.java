package com.example.roy.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;

import java.util.Arrays;
import java.util.List;

public class perfil extends Fragment {

    private Button btnEditar;
    private ListView lvHistorial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflamos el layout del perfil
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        lvHistorial = view.findViewById(R.id.lvHistorial);

        // Datos de ejemplo
        List<String> mock = Arrays.asList(
                "Tienda de campaña 4-5 personas (12/10/25 - 20/10/25)",
                "Balón de basket talla 7 (05/09/25 - 08/09/25)",
                "Parrilla portátil (21/07/25 - 23/07/25)"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                mock
        );
        lvHistorial.setAdapter(adapter);

        return view;
    }
}
