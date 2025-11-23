package com.example.roy.profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;

public class RentalDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITULO = "extra_titulo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rental_detail_activity);

        String titulo = getIntent().getStringExtra(EXTRA_TITULO);
        TextView tv = findViewById(R.id.tvDetalleObjeto);
        if (tv != null) {
            tv.setText(titulo != null ? titulo : "Detalle del objeto");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
