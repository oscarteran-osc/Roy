package com.example.roy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar; // Necesario para el loading
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro3 extends AppCompatActivity implements View.OnClickListener {

    ImageButton back3;
    EditText correo, contra, check;
    Button crear;
    TextView backlogin3;
    ProgressBar progressBar; // Nuevo
    private Bundle datosCompletos; // Para los datos de los 3 pasos
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro3);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        datosCompletos = getIntent().getExtras();

        if (datosCompletos == null) {
            Toast.makeText(this, "Error: Faltan datos de registro.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        back3 = findViewById(R.id.rowregresar3);
        correo = findViewById(R.id.mail);
        contra = findViewById(R.id.contraseña);
        check = findViewById(R.id.checkcontra);
        crear = findViewById(R.id.crearcuenta);
        backlogin3 = findViewById(R.id.backlogin3);
        // Asumiendo que añades un ProgressBar con el ID 'progressBar' en activity_registro3.xml
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        back3.setOnClickListener(this);
        crear.setOnClickListener(this);
        backlogin3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar3){
            // Usar finish() para regresar
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
        // Se pueden agregar más validaciones de formato (correo válido, longitud de contraseña)

        // 1. Mostrar loading
        crear.setEnabled(false);
        crear.setText("Creando...");
        progressBar.setVisibility(View.VISIBLE);

        // 2. Construir el objeto Usuario completo
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(datosCompletos.getString("nombre"));
        nuevoUsuario.setApellido(datosCompletos.getString("apellido"));
        nuevoUsuario.setTelefono(datosCompletos.getString("telefono"));
        nuevoUsuario.setDireccion(datosCompletos.getString("direccion"));
        nuevoUsuario.setCorreo(mail);
        nuevoUsuario.setPassword(password);
        // La fecha de registro (fecharegistro) probablemente la maneja el backend

        // 3. Llamada a Retrofit
        Call<Usuario> call = apiService.registrarUsuario(nuevoUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Registro exitoso. Navegar a Login.
                    Toast.makeText(Registro3.this, "¡Cuenta Creada! Inicia sesión.", Toast.LENGTH_LONG).show();
                    Intent gologin = new Intent(Registro3.this, LoginActivity.class);
                    // Esto limpia todas las pantallas de registro
                    gologin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(gologin);
                } else {
                    // Error del servidor (ej: correo ya registrado, o validación fallida)
                    Toast.makeText(Registro3.this, "Error: Ya existe una cuenta con este correo.", Toast.LENGTH_LONG).show();
                    crear.setEnabled(true);
                    crear.setText("Crear cuenta");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Error de red
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Registro3.this, "Error de conexión: No se pudo registrar. " + t.getMessage(), Toast.LENGTH_LONG).show();
                crear.setEnabled(true);
                crear.setText("Crear cuenta");
            }
        });
    }
}