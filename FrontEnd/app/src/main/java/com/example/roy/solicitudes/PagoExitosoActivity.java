package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.Inicio;
import com.google.android.material.button.MaterialButton;

public class PagoExitosoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagoexitoso);

        // Obtener datos del Intent
        String orderId = getIntent().getStringExtra("orderId");
        String amount = getIntent().getStringExtra("amount");

        // Referencias a las vistas
        TextView tvMonto = findViewById(R.id.tvMontoConfirmacion);
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        MaterialButton btnVolver = findViewById(R.id.btnVolverInicio);

        // ✅ Validar y mostrar información
        if (amount != null) {
            tvMonto.setText("$" + amount + " MXN");
        } else {
            tvMonto.setText("$0.00 MXN");
        }

        if (orderId != null) {
            tvOrderId.setText("ID de orden: " + orderId);
        } else {
            tvOrderId.setText("ID de orden: ---");
        }

        // Botón para regresar al inicio
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}