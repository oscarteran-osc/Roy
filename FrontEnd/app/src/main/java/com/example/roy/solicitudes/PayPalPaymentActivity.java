package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class PayPalPaymentActivity extends AppCompatActivity {

    private int idSolicitud;
    private double monto;
    private String nombreObjeto;

    private Button btnSimularPago, btnCancelar;
    private TextView tvMonto, tvNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypalpayment);

        // Obtener datos del Intent
        idSolicitud = getIntent().getIntExtra("idSolicitud", -1);
        monto = getIntent().getDoubleExtra("monto", 0.0);
        nombreObjeto = getIntent().getStringExtra("nombreObjeto");

        // Inicializar vistas
        tvMonto = findViewById(R.id.tvMontoTotal);
        tvNombre = findViewById(R.id.tvNombreArticulo);
        btnSimularPago = findViewById(R.id.btnSimularPago);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Mostrar información
        tvMonto.setText(String.format("$%.2f", monto));
        tvNombre.setText(nombreObjeto);

        // Botón para simular pago
        btnSimularPago.setOnClickListener(v -> {
            btnSimularPago.setEnabled(false);
            btnSimularPago.setText("Procesando...");

            Toast.makeText(this, "Procesando pago con PayPal...", Toast.LENGTH_SHORT).show();

            // Simular delay de procesamiento
            new Handler().postDelayed(() -> {
                // Generar ID de orden simulado
                String orderId = "PAYPAL-ORDER-" + System.currentTimeMillis();

                // TODO: Aquí llamarías a tu API para confirmar el pago
                // actualizarEstadoEnBackend(idSolicitud, orderId);

                // Ir a pantalla de éxito
                Intent intent = new Intent(PayPalPaymentActivity.this, PagoExitosoActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("monto", monto);
                intent.putExtra("nombreObjeto", nombreObjeto);
                startActivity(intent);
                finish();
            }, 2000); // 2 segundos de simulación
        });

        // Botón cancelar
        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Método para actualizar el backend cuando implementes tu API
    /*
    private void actualizarEstadoEnBackend(int idSolicitud, String orderId) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.confirmarPago(idSolicitud, orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("PayPal", "Estado actualizado en backend");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PayPal", "Error al actualizar backend: " + t.getMessage());
            }
        });
    }
    */
}