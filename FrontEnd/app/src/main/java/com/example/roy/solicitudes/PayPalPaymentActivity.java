package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import kotlin.jvm.functions.Function1;

import com.example.roy.R;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFinishStartResult;
import kotlin.Unit;

public class PayPalPaymentActivity extends AppCompatActivity {

    private static final String TAG = "PayPalPayment";
    private static final String CLIENT_ID = "TU_CLIENT_ID_AQUI";  // ⚠️ Reemplaza con tu Client ID
    private static final String RETURN_URL = "com.example.roy://paypalpay";

    private PayPalWebCheckoutClient payPalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypalpayment);

        // Configurar PayPal
        CoreConfig config = new CoreConfig(CLIENT_ID, Environment.SANDBOX);

        // Inicializar el cliente
        payPalClient = new PayPalWebCheckoutClient(this, config, RETURN_URL);

        // Configurar el botón de pago
        Button payButton = findViewById(R.id.paypalButton);
        payButton.setOnClickListener(v -> iniciarPago());
    }

    private void iniciarPago() {
        // TODO: Obtener el orderId desde tu backend
        String orderId = crearOrdenEnBackend();

        if (orderId != null && !orderId.isEmpty()) {
            PayPalWebCheckoutRequest request = new PayPalWebCheckoutRequest(orderId);
            payPalClient.start(this, request);
        } else {
            Toast.makeText(this, "Error al crear la orden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            PayPalWebCheckoutFinishStartResult result =
                    payPalClient.finishStart(data, RETURN_URL);

            procesarResultado(result);
        }
    }


    private void procesarResultado(PayPalWebCheckoutFinishStartResult result) {
        if (result instanceof PayPalWebCheckoutFinishStartResult.Success) {
            PayPalWebCheckoutFinishStartResult.Success successResult =
                    (PayPalWebCheckoutFinishStartResult.Success) result;

            String orderId = successResult.getOrderId();
            Log.d(TAG, "Pago exitoso - Order ID: " + orderId);
            Toast.makeText(this, "¡Pago completado!", Toast.LENGTH_LONG).show();

            capturarPago(orderId);
            finish();

        } else if (result instanceof PayPalWebCheckoutFinishStartResult.Failure) {
            PayPalWebCheckoutFinishStartResult.Failure failureResult =
                    (PayPalWebCheckoutFinishStartResult.Failure) result;

            PayPalSDKError error = failureResult.getError();
            Log.e(TAG, "Error: " + error.getErrorDescription());
            Toast.makeText(this,
                    "Error en el pago: " + error.getErrorDescription(),
                    Toast.LENGTH_LONG).show();

        } else if (result instanceof PayPalWebCheckoutFinishStartResult.Canceled) {
            Log.d(TAG, "Pago cancelado por el usuario");
            Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
        }
    }

    private String crearOrdenEnBackend() {
        // TODO: Implementar llamada al backend para crear orden
        Toast.makeText(this,
                "Debes implementar la creación de orden en tu backend",
                Toast.LENGTH_LONG).show();

        return null;
    }

    private void capturarPago(String orderId) {
        // TODO: Llamar al backend para capturar el pago
        Log.d(TAG, "Capturando pago para orden: " + orderId);
    }
}