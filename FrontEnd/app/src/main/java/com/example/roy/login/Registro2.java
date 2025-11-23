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

public class Registro2 extends AppCompatActivity implements View.OnClickListener {

    ImageButton back2;
    EditText dom;
    Button siguiente;
    TextView backinicio2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro2);

        back2 = findViewById(R.id.rowregresar2);
        back2.setOnClickListener(this);

        dom = findViewById(R.id.domicilio);

        siguiente = findViewById(R.id.sig2);
        siguiente.setOnClickListener(this);

        backinicio2 = findViewById(R.id.backlogin2);
        backinicio2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rowregresar2){
            Intent regreso = new Intent(this, Registro.class);
            startActivity(regreso);
        }
        else if (id == R.id.sig2) {
            Intent gosig2 = new Intent(this, Registro3.class);
            startActivity(gosig2);
        }
        else if (id == R.id.backlogin2) {
            Intent gobacklogin2 = new Intent(this, LoginActivity.class);
            startActivity(gobacklogin2);
        }
    }
}
