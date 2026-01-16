package com.example.roy.misobjetos;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class detalles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        Button cancelar = findViewById(R.id.cancelar);
        cancelar.setOnClickListener(v -> finish());

        int idObjeto = getIntent().getIntExtra("objetoId", -1);
        // Aquí luego puedes llamar API para traer detalles reales
    }
}
