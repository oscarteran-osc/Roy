package com.example.roy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.roy.R;

public class Registro2 extends AppCompatActivity implements View.OnClickListener {

    ImageButton back2;
    EditText dom;
    Button siguiente;
    TextView backinicio2;

    // Variable para almacenar y actualizar los datos previos
    private Bundle datosPrevios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro2);

        // 1. Recuperar los datos del paso anterior (Registro.java)
        datosPrevios = getIntent().getExtras();
        if (datosPrevios == null) {
            Toast.makeText(this, "Error: Inicia el registro desde el principio.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        back2 = findViewById(R.id.rowregresar2);
        back2.setOnClickListener(this);

        dom = findViewById(R.id.domicilio); // ID del EditText de domicilio

        siguiente = findViewById(R.id.sig2);
        siguiente.setOnClickListener(this);

        backinicio2 = findViewById(R.id.backlogin2);
        backinicio2.setOnClickListener(this);

        // Manejo de insets para EdgeToEdge (si es necesario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar2){
            // Usamos finish() para volver a la pantalla anterior sin crear una nueva
            finish();
        }
        else if (id == R.id.sig2) {
            // Lógica para ir a Registro3
            pasarASiguienteRegistro();
        }
        else if (id == R.id.backlogin2) {
            Intent gobacklogin2 = new Intent(this, LoginActivity.class);
            startActivity(gobacklogin2);
        }
    }

    private void pasarASiguienteRegistro() {
        String domicilio = dom.getText().toString().trim();

        if (domicilio.isEmpty()) {
            Toast.makeText(this, "Ingresa tu domicilio (dirección).", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Agregar los datos de esta pantalla al Bundle existente
        // La clave 'direccion' debe coincidir con el RegisterRequest del backend
        datosPrevios.putString("direccion", domicilio);

        Intent gosig2 = new Intent(this, Registro3.class);
        // 3. Adjuntar el Bundle COMPLETO (Nombre, Apellido, Teléfono, Dirección) al Intent
        gosig2.putExtras(datosPrevios);
        startActivity(gosig2);
    }
}