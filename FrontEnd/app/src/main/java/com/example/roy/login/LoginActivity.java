package com.example.roy.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.Inicio;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton back;
    private EditText etMail, etContra;
    private Button btnIniciar;
    private TextView gobackregister;
    private ProgressBar progressBar;

    private SharedPreferences prefs;
    private ApiService apiService; // Instancia de Retrofit Service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Retrofit y SharedPreferences
        apiService = RetrofitClient.getClient().create(ApiService.class);
        prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);

        // Inicializar vistas
        back = findViewById(R.id.rowlogin);
        etMail = findViewById(R.id.loginmail);
        etContra = findViewById(R.id.logincontra);
        btnIniciar = findViewById(R.id.btnlogin);
        gobackregister = findViewById(R.id.backregister);

        // Asumiendo que has añadido un ProgressBar con el ID 'progressBar' en tu activity_login.xml
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Ocultar al inicio

        back.setOnClickListener(this);
        btnIniciar.setOnClickListener(this);
        gobackregister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.rowlogin) {
            finish();
        } else if (id == R.id.backregister) {
            Intent goregister = new Intent(this, Registro.class);
            startActivity(goregister);
        } else if (id == R.id.btnlogin) {
            realizarLogin();
        }
    }

    private void realizarLogin() {
        String correo = etMail.getText().toString().trim();
        String password = etContra.getText().toString().trim();

        // 1. Validaciones
        if (correo.isEmpty()) {
            etMail.setError("Ingresa tu correo");
            etMail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etContra.setError("Ingresa tu contraseña");
            etContra.requestFocus();
            return;
        }

        // 2. Prepara la UI (Mostrar loading)
        btnIniciar.setEnabled(false);
        btnIniciar.setText("Iniciando...");
        progressBar.setVisibility(View.VISIBLE);

        // 3. Crea el objeto de credenciales para enviar al servidor
        Usuario loginCredentials = new Usuario();
        loginCredentials.setCorreo(correo);
        loginCredentials.setPassword(password);

        // 4. Realiza la llamada con Retrofit
        Call<Usuario> call = apiService.loginUser(loginCredentials);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // Ocultar loading
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    // Login exitoso
                    guardarSesion(usuario);
                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + usuario.getNombre() + "!", Toast.LENGTH_SHORT).show();

                    // Navegar a la actividad principal
                    Intent intent = new Intent(LoginActivity.this, Inicio.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Login fallido (Ej: 401 Unauthorized, o error lógico del servidor)
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas o error en el servidor.", Toast.LENGTH_LONG).show();
                    btnIniciar.setEnabled(true);
                    btnIniciar.setText("Iniciar Sesión");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Ocultar loading y manejar error de red (sin conexión, timeout, etc.)
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
            }
        });
    }

    private void guardarSesion(Usuario usuario) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("userId", usuario.getIdUsuario() != null ? usuario.getIdUsuario() : -1);
        editor.putString("userName", usuario.getNombre());
        editor.putString("userEmail", usuario.getCorreo());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}