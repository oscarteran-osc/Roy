package com.example.roy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.AuthResponse; // Importar AuthResponse
import com.example.roy.models.RegisterRequest; // Importar RegisterRequest

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro3 extends AppCompatActivity implements View.OnClickListener {

    ImageButton back3;
    EditText correo, contra, check;
    Button crear;
    TextView backlogin3;
    ProgressBar progressBar;
    private Bundle datosCompletos;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro3);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        datosCompletos = getIntent().getExtras();

        if (datosCompletos == null) {
            // Este error ocurre si no se pasaron datos desde Registro2
            Toast.makeText(this, "Error crítico: Datos de registro incompletos.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        back3 = findViewById(R.id.rowregresar3);
        correo = findViewById(R.id.mail);
        contra = findViewById(R.id.contraseña);
        check = findViewById(R.id.checkcontra);
        crear = findViewById(R.id.crearcuenta);
        backlogin3 = findViewById(R.id.backlogin3);
        progressBar = findViewById(R.id.progressBar); // Asegúrate que el ID es correcto
        progressBar.setVisibility(View.GONE);

        back3.setOnClickListener(this);
        crear.setOnClickListener(this);
        backlogin3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar3){
            finish();
        } else if (id == R.id.backlogin3) {
            Intent gologin3 = new Intent(this, LoginActivity.class);
            startActivity(gologin3);
        } else if (id == R.id.crearcuenta) {
            realizarRegistro();
        }
    }

    private void realizarRegistro() {
        String mail = correo.getText().toString().trim();
        String password = contra.getText().toString().trim();
        String confirmPassword = check.getText().toString().trim();

        // Validaciones Finales
        if (mail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Completa correo y contraseñas.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Mostrar loading
        crear.setEnabled(false);
        crear.setText("Creando...");
        progressBar.setVisibility(View.VISIBLE);

        // 2. Construir el objeto RegisterRequest COMPLETO
        RegisterRequest nuevoRegistro = new RegisterRequest();
        nuevoRegistro.setNombre(datosCompletos.getString("nombre"));
        nuevoRegistro.setApellido(datosCompletos.getString("apellido"));
        nuevoRegistro.setTelefono(datosCompletos.getString("telefono"));
        nuevoRegistro.setDireccion(datosCompletos.getString("direccion"));
        nuevoRegistro.setCorreo(mail);
        nuevoRegistro.setPassword(password);

        // ========== LOGS CRÍTICOS ==========
        android.util.Log.d("REGISTRO", "=== INICIANDO REGISTRO ===");
        android.util.Log.d("REGISTRO", "Nombre: " + datosCompletos.getString("nombre"));
        android.util.Log.d("REGISTRO", "Apellido: " + datosCompletos.getString("apellido"));
        android.util.Log.d("REGISTRO", "Teléfono: " + datosCompletos.getString("telefono"));
        android.util.Log.d("REGISTRO", "Dirección: " + datosCompletos.getString("direccion"));
        android.util.Log.d("REGISTRO", "Correo: " + mail);
        android.util.Log.d("REGISTRO", "URL: " + apiService.toString());
        // ===================================

        // 3. Llamada a Retrofit
        apiService.registrarUsuario(nuevoRegistro).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(View.GONE);

                // ========== LOGS DE RESPUESTA ==========
                android.util.Log.d("REGISTRO", "=== RESPUESTA RECIBIDA ===");
                android.util.Log.d("REGISTRO", "Código HTTP: " + response.code());
                android.util.Log.d("REGISTRO", "Mensaje: " + response.message());

                if (response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        android.util.Log.e("REGISTRO", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e("REGISTRO", "No se pudo leer errorBody");
                    }
                }

                if (response.body() != null) {
                    android.util.Log.d("REGISTRO", "Body: " + response.body().toString());
                }
                // ======================================

                if (response.isSuccessful()) {
                    Toast.makeText(Registro3.this, "¡Cuenta Creada! Inicia sesión.", Toast.LENGTH_LONG).show();
                    Intent gologin = new Intent(Registro3.this, LoginActivity.class);
                    gologin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gologin);
                } else if (response.code() == 409) {
                    Toast.makeText(Registro3.this, "Error: Ya existe una cuenta con este correo.", Toast.LENGTH_LONG).show();
                    crear.setEnabled(true);
                    crear.setText("Crear cuenta");
                } else {
                    Toast.makeText(Registro3.this, "Error al crear cuenta. Código: " + response.code(), Toast.LENGTH_LONG).show();
                    crear.setEnabled(true);
                    crear.setText("Crear cuenta");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                // ========== LOG DE FALLO ==========
                android.util.Log.e("REGISTRO", "=== ERROR DE CONEXIÓN ===");
                android.util.Log.e("REGISTRO", "Mensaje: " + t.getMessage());
                t.printStackTrace();
                // ==================================

                Toast.makeText(Registro3.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                crear.setEnabled(true);
                crear.setText("Crear cuenta");
            }
        });
    }

}