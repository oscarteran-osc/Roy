package com.example.roy.solicitudes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.network.RetrofitClient;
import com.example.roy.network.models.PayPalOrderResponse;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFinishStartResult;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalPaymentActivity extends AppCompatActivity {

    private static final String TAG = "PayPalPayment";
    private static final String CLIENT_ID = "Ad2Ti-cFyuNpJqkSu9EBQ21TlAjC3jhNtwXaujBU8NBX5t1cZeSIMe03RrTcnQMYzlZZmplVlRAkDxaL";
    private static final String RETURN_URL = "com.example.roy://paypalpay";

    // ✅ Claves para guardar el estado
    private static final String KEY_ID_SOLICITUD = "idSolicitud";
    private static final String KEY_MONTO = "montoAPagar";

    private PayPalWebCheckoutClient payPalClient;

    private int idSolicitud;
    private double montoAPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypalpayment);

        Log.d(TAG, "🎯 PayPalPaymentActivity onCreate");

        // ✅ Restaurar datos guardados o leer del intent
        if (savedInstanceState != null) {
            idSolicitud = savedInstanceState.getInt(KEY_ID_SOLICITUD, 0);
            montoAPagar = savedInstanceState.getDouble(KEY_MONTO, 100.0);
            Log.d(TAG, "♻️ Datos restaurados desde savedInstanceState");
        } else {
            idSolicitud = getIntent().getIntExtra("ID_SOLICITUD", 0);
            montoAPagar = getIntent().getDoubleExtra("MONTO", 100.0);
        }



            // ✅ Mostrar el monto en la UI
            TextView tvMonto = findViewById(R.id.tvMontoTotal); // Ajusta el ID según tu layout
            if (tvMonto != null) {
                tvMonto.setText("$" + String.format("%.2f", montoAPagar) + " MXN");
            }



        Log.d(TAG, "📋 ID Solicitud: " + idSolicitud);
        Log.d(TAG, "💰 Monto a pagar: $" + montoAPagar);

        CoreConfig config = new CoreConfig(CLIENT_ID, Environment.SANDBOX);
        payPalClient = new PayPalWebCheckoutClient(this, config, RETURN_URL);

        Button payButton = findViewById(R.id.paypalButton);
        payButton.setOnClickListener(v -> iniciarPago());

        // ✅ Botón cancelar cierra la pantalla
        Button btnCancelar = findViewById(R.id.btnCancelar);
        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> finish());
        }

        handleDeepLink(getIntent());
    }

    // ✅ Guardar el estado antes de que la actividad sea destruida
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ID_SOLICITUD, idSolicitud);
        outState.putDouble(KEY_MONTO, montoAPagar);
        Log.d(TAG, "💾 Estado guardado: ID=" + idSolicitud + ", Monto=" + montoAPagar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.d(TAG, "📥 onNewIntent llamado");
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent == null) {
            Log.w(TAG, "⚠️ Intent es null");
            return;
        }

        Uri data = intent.getData();

        if (data != null) {
            Log.d(TAG, "🔗 Deep Link recibido: " + data.toString());
            Log.d(TAG, "   Scheme: " + data.getScheme());
            Log.d(TAG, "   Host: " + data.getHost());
            Log.d(TAG, "   Path: " + data.getPath());
            Log.d(TAG, "   Query: " + data.getQuery());

            String token = data.getQueryParameter("token");
            String payerId = data.getQueryParameter("PayerID");
            String paymentId = data.getQueryParameter("paymentId");

            if (token != null && payerId != null) {
                Log.d(TAG, "✅ Pago autorizado por el usuario");
                Log.d(TAG, "   Token: " + token);
                Log.d(TAG, "   PayerID: " + payerId);
                Log.d(TAG, "   PaymentID: " + paymentId);

                // Capturar el pago
                capturarPago(paymentId != null ? paymentId : token, payerId);

            } else {
                Log.e(TAG, "❌ Faltan parámetros en el deep link");
                mostrarError("Error: respuesta incompleta de PayPal");
            }

        } else {
            Log.d(TAG, "ℹ️ No hay deep link data en el intent");
        }
    }

    private void capturarPago(String paymentId, String payerId) {
        Log.d(TAG, "🔄 Capturando pago en el servidor...");
        Log.d(TAG, "   PaymentID: " + paymentId);
        Log.d(TAG, "   PayerID: " + payerId);
        Log.d(TAG, "   ID Solicitud: " + idSolicitud);

        Map<String, Object> request = new HashMap<>();
        request.put("paymentId", paymentId);
        request.put("payerId", payerId);
        request.put("idSolicitud", idSolicitud);

        Log.d(TAG, "🌐 URL: http://10.0.2.2:8080/api/paypal/capturar-pago");
        Log.d(TAG, "📤 Request Body: " + request.toString());

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.capturarPago(request);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Pago capturado exitosamente");
                    Log.d(TAG, "   Respuesta: " + response.body().toString());

                    // ✅ Redirigir a pantalla de éxito con los datos correctos
                    Intent intent = new Intent(PayPalPaymentActivity.this, PagoExitosoActivity.class);
                    intent.putExtra("orderId", paymentId);
                    intent.putExtra("amount", String.valueOf(montoAPagar));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "❌ Error en respuesta: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "   Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mostrarError("Error al procesar el pago (Código: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "❌ Error de red al capturar pago: " + t.getMessage());
                t.printStackTrace();
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void iniciarPago() {
        Log.d(TAG, "🔄 Iniciando proceso de pago...");

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // ✅ PayPal Sandbox solo acepta USD. Convertimos MXN → USD (tipo de cambio aprox. 17)
        double montoUSD = montoAPagar / 17.0;
        montoUSD = Math.round(montoUSD * 100.0) / 100.0; // redondear a 2 decimales

        Call<PayPalOrderResponse> call = apiService.crearOrdenPayPal(
                montoUSD,
                "USD",
                "Pago de renta - Solicitud #" + idSolicitud
        );

        call.enqueue(new Callback<PayPalOrderResponse>() {
            @Override
            public void onResponse(Call<PayPalOrderResponse> call, Response<PayPalOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String orderId = response.body().getOrderId();
                    Log.d(TAG, "✅ Orden creada: " + orderId);

                    PayPalWebCheckoutRequest request = new PayPalWebCheckoutRequest(orderId);

                    // ✅ Iniciar PayPal sin listener - el resultado llega por deep link
                    payPalClient.start(PayPalPaymentActivity.this, request);

                } else {
                    Log.e(TAG, "❌ Error al crear orden: " + response.code());
                    Toast.makeText(PayPalPaymentActivity.this,
                            "Error al crear la orden de pago", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PayPalOrderResponse> call, Throwable t) {
                Log.e(TAG, "❌ Error de red: " + t.getMessage());
                Toast.makeText(PayPalPaymentActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}

