package com.example.roy.misobjetos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.roy.R;

public class plantillaagregar extends AppCompatActivity implements View.OnClickListener {

    Button agregadito, canceladito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plantillaagregar);

        agregadito = findViewById(R.id.agregarobj);
        canceladito = findViewById(R.id.cancelar);
        agregadito.setOnClickListener(this);
        canceladito.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String cadenita = ((Button) v).getText().toString();
        if (cadenita.equals("Cancelar")) {
            Intent intent = new Intent(this, MisObjetos.class);
            startActivity(intent);
        } else if (cadenita.equals("Agregar")) {
            Toast.makeText(this, "¡Objeto agregado!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MisObjetos.class);
            startActivity(intent);
        }
    }
}