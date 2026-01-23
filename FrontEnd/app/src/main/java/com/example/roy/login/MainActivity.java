package com.example.roy.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.Inicio;
import com.example.roy.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ PRIMERO: Verificar si ya hay sesión activa
        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String token = prefs.getString("token", null);

        if (userId != -1 && token != null && !token.isEmpty()) {
            // ✅ Ya hay sesión activa → ir directo a Inicio
            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // ⚠️ IMPORTANTE: detener la ejecución aquí
        }

        // ✅ No hay sesión → mostrar pantalla de bienvenida
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.btniniciosesion);
        login.setOnClickListener(this);

        register = findViewById(R.id.registrarse);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.registrarse) {
            Intent goregister = new Intent(this, Registro.class);
            startActivity(goregister);
        }
        else if (id == R.id.btniniciosesion) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}