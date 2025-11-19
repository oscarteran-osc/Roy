package com.example.roy.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class Objeto extends AppCompatActivity implements View.OnClickListener {

    ImageButton atrasito;
    Button solicitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_objeto); // Renombra este archivo a activity_objeto.xml si quieres

        atrasito = findViewById(R.id.back);
        solicitud = findViewById(R.id.enviosoli);

        atrasito.setOnClickListener(this);
        solicitud.setOnClickListener(this);

        // Cargar el fragment objetito
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorfragmentos, new objetito())
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
            Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
        }
    }
}