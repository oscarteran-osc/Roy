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

import com.example.roy.Inicio;
import com.example.roy.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    EditText mail, contra;
    Button btniniciar;
    TextView gobackregister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        back = findViewById(R.id.rowlogin);
        back.setOnClickListener(this);

        mail = findViewById(R.id.loginmail);
        contra = findViewById(R.id.logincontra);

        btniniciar = findViewById(R.id.btnlogin);
        btniniciar.setOnClickListener(this);

        gobackregister = findViewById(R.id.backregister);
        gobackregister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowlogin){
            finish();
        }
        else if (id == R.id.backregister) {
            Intent goregister = new Intent(this, Registro.class);
            startActivity(goregister);
        }
        else if (id == R.id.btnlogin) {
            Toast.makeText(this, "¡Sesión Iniciada!", Toast.LENGTH_SHORT).show();
            Intent goregister = new Intent(this, Inicio.class);
            startActivity(goregister);
        }
    }
}