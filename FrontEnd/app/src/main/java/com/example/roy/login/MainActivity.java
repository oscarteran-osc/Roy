package com.example.roy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button login, register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (id == R.id.registrarse){
            Intent goregister = new Intent(this, Registro.class);
            startActivity(goregister);
        }
        else if (id == R.id.btniniciosesion) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}