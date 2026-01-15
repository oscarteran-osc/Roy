package com.example.roy.login;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.roy.Inicio;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.AuthResponse; // Importar AuthResponse
import com.example.roy.models.LoginRequest; // Importar LoginRequest
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;
    private ApiService apiService;
    private EditText etMail, etContra;
    private Button btnIniciar;
    private TextView goBackRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Retrofit y SharedPreferences
        apiService = RetrofitClient.getClient().create(ApiService.class);
        prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);


        // Inicializar vistas
        etMail = findViewById(R.id.loginmail); // Asegúrate que el ID sea correcto
        etContra = findViewById(R.id.logincontra); // Asegúrate que el ID sea correcto
        btnIniciar = findViewById(R.id.btnlogin);
        goBackRegister = findViewById(R.id.backregister);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnIniciar.setOnClickListener(this);
        goBackRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnlogin) {
            realizarLogin();
        } else if (id == R.id.backregister) {
            Intent goRegister = new Intent(this, Registro.class);
            startActivity(goRegister);
        }
        else if (id == R.id.rowlogin){
            Intent intentito = new Intent(this, MainActivity.class);
            startActivity(intentito);
            finish();

        }
    }

    private void realizarLogin() {
        String email = etMail.getText().toString().trim();
        String password = etContra.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Mostrar loading
        btnIniciar.setEnabled(false);
        btnIniciar.setText("Iniciando...");
        progressBar.setVisibility(View.VISIBLE);

        // 2. Construir el objeto LoginRequest
        LoginRequest credenciales = new LoginRequest(email, password);

        // 3. Llamada a Retrofit y espera AuthResponse
        apiService.loginUser(credenciales).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
                progressBar.setVisibility(View.GONE);

                prefs.edit()
                        .putString("TOKEN", response.body().getToken())
                        .putInt("ID_USUARIO", response.body().getIdUsuario())
                        .apply();

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    // ✅ CAMBIO: Obtener directamente del AuthResponse
                    Integer userId = authResponse.getIdUsuario();
                    String token = authResponse.getToken();

                    // ✅ Validar que los datos no sean nulos
                    if (userId != null && token != null) {
                        // Guardar ID de usuario y Token
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId);
                        editor.putString("token", token);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "¡Bienvenido " + authResponse.getNombre() + "!", Toast.LENGTH_SHORT).show();

                        // Navegar a la pantalla principal
                        Intent goinicio = new Intent(LoginActivity.this, Inicio.class);
                        startActivity(goinicio);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al procesar respuesta del servidor.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}