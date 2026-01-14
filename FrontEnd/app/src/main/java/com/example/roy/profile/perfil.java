package com.example.roy.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.login.MainActivity;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class perfil extends Fragment {

    private Button btnLogout;
    private ListView lvHistorial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        lvHistorial = view.findViewById(R.id.lvHistorial);
        btnLogout = view.findViewById(R.id.btnLogout);

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

        // ✅ LISTENER DEL LOGOUT
        btnLogout.setOnClickListener(v -> mostrarDialogoLogout());

        return view;
    }

    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí, cerrar", (dialog, which) -> realizarLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarLogout() {
        // Limpiar SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // Redirigir al MainActivity
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}