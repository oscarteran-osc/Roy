package com.example.roy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.roy.home.Home;
import com.example.roy.misobjetos.MisObjetos;
import com.example.roy.profile.PerfilFragment;
import com.example.roy.solicitudes.solicitudes;
import com.example.roy.login.LoginActivity; // ✅ Agregar este import

public class Inicio extends AppCompatActivity {

    LinearLayout home, box, mail, profile;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ VERIFICAR SESIÓN ANTES DE MOSTRAR LA PANTALLA
        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            // Si no hay sesión, redirigir al login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // ✅ FIN DE VERIFICACIÓN

        setContentView(R.layout.activity_inicio);

        home = findViewById(R.id.nav_home_container);
        box = findViewById(R.id.nav_obj_container);
        mail = findViewById(R.id.nav_mail_container);
        profile = findViewById(R.id.nav_profile_container);

        fragmentManager = getSupportFragmentManager();
        loadFragment(new Home());

        home.setOnClickListener(v -> {
            limpiarSeleccion();
            home.setSelected(true);
            loadFragment(new Home());
        });

        box.setOnClickListener(v -> {
            limpiarSeleccion();
            box.setSelected(true);
            loadFragment(new MisObjetos());
        });

        mail.setOnClickListener(v -> {
            limpiarSeleccion();
            mail.setSelected(true);
            loadFragment(new solicitudes());
        });

        profile.setOnClickListener(v -> {
            limpiarSeleccion();
            profile.setSelected(true);
            loadFragment(new PerfilFragment());
        });
    }

    private void limpiarSeleccion() {
        home.setSelected(false);
        box.setSelected(false);
        mail.setSelected(false);
        profile.setSelected(false);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}