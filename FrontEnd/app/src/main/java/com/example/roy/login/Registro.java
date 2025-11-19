package com.example.roy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
            Intent gosig1 = new Intent(this, Registro2.class);
            startActivity(gosig1);
        }
        else if (id == R.id.backlogin) {
            Intent gobacklogin = new Intent(this, LoginActivity.class);
            startActivity(gobacklogin);
        }
    }

}