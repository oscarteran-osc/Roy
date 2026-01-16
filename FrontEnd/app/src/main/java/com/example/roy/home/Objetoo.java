package com.example.roy.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class Objetoo extends AppCompatActivity implements View.OnClickListener {

    ImageButton atrasito;
    Button solicitud;
    private int objetoId = -1; // ✅ Guardar el ID del objeto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_objeto);

        atrasito = findViewById(R.id.back);
        solicitud = findViewById(R.id.enviosoli);

        atrasito.setOnClickListener(this);
        solicitud.setOnClickListener(this);

        // ✅ Obtener el ID del objeto del Intent
        if (getIntent() != null) {
            objetoId = getIntent().getIntExtra("objetoId", -1);
        }

        // ✅ Cargar el fragment objetito con el ID
        if (savedInstanceState == null) {
            // Crear el fragment con el ID del objeto
            objetito fragment = objetito.newInstance(objetoId);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorfragmentos, fragment)
                    .commit();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.back){
            finish(); // Cierra la activity y regresa a la anterior
        }
        else if(id == R.id.enviosoli){
            // ✅ Opcional: pasar el objetoId a la siguiente pantalla si es necesario
            if (objetoId != -1) {
                Toast.makeText(this, "Solicitud enviada para objeto #" + objetoId,
                        Toast.LENGTH_SHORT).show();
                // Aquí podrías abrir otra Activity/Fragment para la solicitud
                // y pasarle el objetoId
            } else {
                Toast.makeText(this, "Error: Objeto no válido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}