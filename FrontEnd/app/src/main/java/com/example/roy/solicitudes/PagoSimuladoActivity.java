package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.Inicio;
import com.example.roy.R;
import com.google.android.material.button.MaterialButton; // Importa MaterialButton

public class PagoSimuladoActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvEstado;
    private TextView tvMonto;
    private MaterialButton btnVolver; // Cambia Button por MaterialButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_simulado);

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar);
        tvEstado = findViewById(R.id.tvEstado);
        tvMonto = findViewById(R.id.tvMonto);
        btnVolver = findViewById(R.id.btnVolver); // Ahora funciona

        // Obtener monto del intent
        double monto = getIntent().getDoubleExtra("monto", 0.0);
        int idSolicitud = getIntent().getIntExtra("idSolicitud", -1);

        if (monto == 0.0) {
            // Si no recibiste monto, usa un valor por defecto para pruebas
            monto = 350.00;
        }

        tvMonto.setText(String.format("$%.2f", monto));
        btnVolver.setEnabled(false);
        btnVolver.setVisibility(View.GONE);

        // Simular proceso de pago
        tvEstado.setText("Procesando pago con PayPal...");

        // Usa Handler con Looper para evitar problemas
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            tvEstado.setText("✅ ¡Pago exitoso!");
            btnVolver.setEnabled(true);
            btnVolver.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Pago completado correctamente", Toast.LENGTH_LONG).show();
        }, 3000); // 3 segundos de simulación

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Evitar que el usuario regrese con el botón atrás
        // Si quieres permitirlo, quita este método
    }
}