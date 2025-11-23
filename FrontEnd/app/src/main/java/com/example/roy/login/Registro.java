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

import com.example.roy.R;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    EditText nom, apellido, num;
    Button siguiente;
    TextView backinicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        back = findViewById(R.id.rowregresar);
        back.setOnClickListener(this);

        nom = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        num = findViewById(R.id.num);

        siguiente = findViewById(R.id.sig1);
        siguiente.setOnClickListener(this);

        backinicio = findViewById(R.id.backlogin);
        backinicio.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar){
            finish();
        }
        else if (id == R.id.sig1) {
            // ---> INICIO DE CORRECCIÓN <---
            String nombre = nom.getText().toString().trim();
            String apell = apellido.getText().toString().trim();
            String numero = num.getText().toString().trim();

            if (nombre.isEmpty() || apell.isEmpty() || numero.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent gosig1 = new Intent(this, Registro2.class);
            // 1. Crear el Bundle y añadir datos
            Bundle bundle = new Bundle();
            bundle.putString("nombre", nombre);
            bundle.putString("apellido", apell);
            bundle.putString("telefono", numero); // Usamos 'telefono' para coincidir con el backend

            // 2. Adjuntar el Bundle al Intent
            gosig1.putExtras(bundle);
            startActivity(gosig1);
            // ---> FIN DE CORRECCIÓN <---
        }
        else if (id == R.id.backlogin) {
            Intent gobacklogin = new Intent(this, LoginActivity.class);
            startActivity(gobacklogin);
        }
    }

}