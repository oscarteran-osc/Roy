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

public class Registro3 extends AppCompatActivity implements View.OnClickListener {

    ImageButton back3;
    EditText correo, contra, check;
    Button crear;
    TextView backlogin3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro3);

        back3 = findViewById(R.id.rowregresar3);
        back3.setOnClickListener(this);

        correo = findViewById(R.id.mail);
        contra = findViewById(R.id.contraseña);
        check = findViewById(R.id.checkcontra);

        crear = findViewById(R.id.crearcuenta);
        crear.setOnClickListener(this);

        backlogin3 = findViewById(R.id.backlogin3);
        backlogin3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar3){
            Intent regreso = new Intent(this, Registro2.class);
            startActivity(regreso);
        }
        else if (id == R.id.backlogin3) {
            Intent gologin3 = new Intent(this, LoginActivity.class);
            startActivity(gologin3);
        }
        else if (id == R.id.crearcuenta) {
            Toast.makeText(this, "¡Cuenta Creada!", Toast.LENGTH_SHORT).show();
            Intent gologin3 = new Intent(this, LoginActivity.class);
            startActivity(gologin3);
        }
    }
}