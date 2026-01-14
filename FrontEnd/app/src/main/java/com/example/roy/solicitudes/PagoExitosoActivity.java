package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.Inicio;
import com.example.roy.R;

public class PagoExitosoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagoexitoso);

        String orderId = getIntent().getStringExtra("orderId");
        double monto = getIntent().getDoubleExtra("monto", 0.0);

        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvMonto = findViewById(R.id.tvMontoConfirmacion);
        Button btnVolver = findViewById(R.id.btnVolverInicio);

        tvOrderId.setText("ID de orden: " + orderId);
        tvMonto.setText(String.format("$%.2f", monto));

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Evitar que regrese, debe usar el botón
        Intent intent = new Intent(this, Inicio.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}