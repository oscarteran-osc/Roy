package com.example.roy.misobjetos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class detalles extends AppCompatActivity {

    private ImageView mainimg, mini1, mini2, mini3;
    private TextView tvNombreObjeto, tvPrecioObjeto, tvCategoriaObjeto, descobj;
    private MaterialButton btnEditar, btnBorrar, cancelar;

    private ApiService apiService;
    private int objetoId;
    private Objeto objetoActual;

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Se editó correctamente -> recargar
                    cargarDetallesObjeto();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        objetoId = getIntent().getIntExtra("objetoId", -1);
        if (objetoId == -1) {
            Toast.makeText(this, "Error: Objeto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        cargarDetallesObjeto();
    }

    private void initViews() {
        mainimg = findViewById(R.id.mainimg);
        mini1 = findViewById(R.id.mini1);
        mini2 = findViewById(R.id.mini2);
        mini3 = findViewById(R.id.mini3);

        tvNombreObjeto = findViewById(R.id.tvNombreObjeto);
        tvPrecioObjeto = findViewById(R.id.tvPrecioObjeto);
        tvCategoriaObjeto = findViewById(R.id.tvCategoriaObjeto);
        descobj = findViewById(R.id.descobj);

        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        cancelar = findViewById(R.id.cancelar);
    }

    private void setupListeners() {
        cancelar.setOnClickListener(v -> finish());

        btnEditar.setOnClickListener(v -> {
            if (objetoActual != null) {
                Intent intent = new Intent(detalles.this, editarobjeto.class);
                intent.putExtra("objetoId", objetoId);
                editLauncher.launch(intent);
            }
        });

        btnBorrar.setOnClickListener(v -> confirmarBorrado());

        mini1.setOnClickListener(v -> {
            if (objetoActual != null && objetoActual.getImagenUrl() != null) {
                cargarImagen(mainimg, objetoActual.getImagenUrl());
            }
        });
    }

    private void cargarDetallesObjeto() {
        apiService.getObjetoPorId(objetoId).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetoActual = response.body();
                    mostrarDetalles(objetoActual);
                } else {
                    Toast.makeText(detalles.this, "Error al cargar el objeto", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                Toast.makeText(detalles.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void mostrarDetalles(Objeto objeto) {
        tvNombreObjeto.setText(objeto.getNombreObjeto());
        tvPrecioObjeto.setText(String.format("$%.0f", objeto.getPrecio()));

        String categoria = objeto.getCategoria() != null ? objeto.getCategoria() : "Sin categoría";
        tvCategoriaObjeto.setText(categoria);

        String descripcion = (objeto.getDescripcion() != null && !objeto.getDescripcion().isEmpty())
                ? objeto.getDescripcion()
                : "Sin descripción";
        descobj.setText(descripcion);

        cargarImagenes(objeto);
    }

    private void cargarImagenes(Objeto objeto) {
        if (objeto.getImagenUrl() != null && !objeto.getImagenUrl().isEmpty()) {
            cargarImagen(mainimg, objeto.getImagenUrl());
            cargarImagen(mini1, objeto.getImagenUrl());
        } else {
            mainimg.setImageResource(R.drawable.casacampania);
            mini1.setImageResource(R.drawable.casacampania);
        }

        mini2.setVisibility(View.GONE);
        mini3.setVisibility(View.GONE);
    }

    private void cargarImagen(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.casacampania)
                .error(R.drawable.casacampania)
                .centerCrop()
                .into(imageView);
    }

    private void confirmarBorrado() {
        new AlertDialog.Builder(this)
                .setTitle("Borrar objeto")
                .setMessage("¿Seguro que quieres borrar este objeto? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, borrar", (dialog, which) -> borrarObjeto())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarObjeto() {
        apiService.eliminarObjeto(objetoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(detalles.this, "Objeto borrado ✅", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(detalles.this, "No se pudo borrar. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(detalles.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
