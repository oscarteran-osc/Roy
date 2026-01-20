package com.example.roy.misobjetos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class detalles extends AppCompatActivity implements View.OnClickListener {

    private Button btnCancelar;
    private int objetoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        inicializarVistas();
        obtenerDatosIntent();
        configurarListeners();
    }

    private void inicializarVistas() {
        btnCancelar = findViewById(R.id.cancelar);
    }

    private void obtenerDatosIntent() {
        objetoId = getIntent().getIntExtra("objetoId", -1);
        // Aquí luego puedes llamar API para traer detalles reales
        if (objetoId != -1) {
            cargarDetallesObjeto(objetoId);
        }
    }

    private void configurarListeners() {
        btnCancelar.setOnClickListener(this);
    }

    private void cargarDetallesObjeto(int id) {
        // TODO: Implementar llamada API para obtener detalles
        // apiService.getObjetoPorId(id).enqueue(...);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelar) {
            finish();
        }
    }
}