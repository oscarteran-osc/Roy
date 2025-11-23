package com.example.roy.misobjetos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.roy.R;

public class detalles extends AppCompatActivity implements View.OnClickListener {
    Button canc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles);
        canc = findViewById(R.id.cancelar);
        canc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Navigation.findNavController(v).navigate(R.id.listaobjetos);
    }
}