package com.example.roy.solicitudes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.roy.R;

public class metododepago extends AppCompatActivity {

    private CardView cardTarjeta, cardPaypal, cardGooglePay;
    private RadioButton radioTarjeta, radioPaypal, radioGooglePay;
    private Button btnContinuar;

    private int idSolicitud;
    private double monto;
    private String nombreObjeto;
    private String metodoSeleccionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metododepago);

        // Obtener datos del intent
        idSolicitud = getIntent().getIntExtra("idSolicitud", -1);
        monto = getIntent().getDoubleExtra("monto", 0.0);
        nombreObjeto = getIntent().getStringExtra("nombreObjeto");

        // Inicializar vistas
        cardTarjeta = findViewById(R.id.payment_option_container);
        cardPaypal = findViewById(R.id.payment_option_container2);
        cardGooglePay = findViewById(R.id.payment_option_container3);

        radioTarjeta = findViewById(R.id.selection_circle);
        radioPaypal = findViewById(R.id.selection_circle2);
        radioGooglePay = findViewById(R.id.selection_circle3);

        btnContinuar = findViewById(R.id.btnContinuarPago);

        // Listeners para las tarjetas
        cardTarjeta.setOnClickListener(v -> seleccionarMetodo("TARJETA", radioTarjeta));
        cardPaypal.setOnClickListener(v -> seleccionarMetodo("PAYPAL", radioPaypal));
        cardGooglePay.setOnClickListener(v -> seleccionarMetodo("GOOGLEPAY", radioGooglePay));

        // Listener para el botón
        btnContinuar.setOnClickListener(v -> procesarPago());


    }

    private void seleccionarMetodo(String metodo, RadioButton radio) {
        // Limpiar todas las selecciones
        radioTarjeta.setChecked(false);
        radioPaypal.setChecked(false);
        radioGooglePay.setChecked(false);

        // Marcar la seleccionada
        radio.setChecked(true);
        metodoSeleccionado = metodo;
    }

    private void procesarPago() {
        if (metodoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        if (metodoSeleccionado.equals("PAYPAL")) {
            // Ir a la integración de PayPal
            Intent intent = new Intent(this, PayPalPaymentActivity.class);
            intent.putExtra("idSolicitud", idSolicitud);
            intent.putExtra("monto", monto);
            intent.putExtra("nombreObjeto", nombreObjeto);
            startActivity(intent);
        } else {
            // Para otros métodos, mostrar "próximamente"
            Toast.makeText(this,
                    "Método " + metodoSeleccionado + " próximamente disponible",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
