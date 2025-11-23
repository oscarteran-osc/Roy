package com.example.roy.misobjetos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService; // Importar ApiService
import com.example.roy.api.RetrofitClient; // Importar RetrofitClient
import com.example.roy.models.Objeto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class plantillaagregar extends AppCompatActivity implements View.OnClickListener {

    private EditText etNombre, etPrecio, etDescripcion;
    private Spinner spinnerCategoria;
    private Button btnAgregar, btnCancelar;
    private ApiService apiService; // ✅ Reemplaza a MockDataManager
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantillaagregar);

        // Inicializar Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Obtener usuario actual
        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1); // Usar -1 o un valor seguro si no está logueado

        // Inicializar vistas
        etNombre = findViewById(R.id.nomobj);
        etPrecio = findViewById(R.id.precioobj);
        etDescripcion = findViewById(R.id.descobj);
        spinnerCategoria = findViewById(R.id.catobj);
        btnAgregar = findViewById(R.id.agregarobj);
        btnCancelar = findViewById(R.id.cancelar);

        // CONFIGURAR SPINNER DE CATEGORÍAS
        String[] categorias = {"Seleccionar categoría", "Tecnología", "Deportes", "Camping",
                "Herramientas", "Eventos", "Transporte", "Otros"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterSpinner);

        btnAgregar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.cancelar) {
            finish();
        }
        else if (id == R.id.agregarobj) {
            agregarObjeto();
        }
    }

    private void agregarObjeto() {
        // VALIDACIONES
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Debes iniciar sesión para agregar objetos.", Toast.LENGTH_LONG).show();
            return;
        }

        // ... (otras validaciones de campos como ya las tenías) ...

        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa un nombre");
            etNombre.requestFocus();
            return;
        }

        if (precioStr.isEmpty()) {
            etPrecio.setError("Ingresa un precio");
            etPrecio.requestFocus();
            return;
        }

        if (categoria.equals("Seleccionar categoría")) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty()) {
            etDescripcion.setError("Agrega una descripción");
            etDescripcion.requestFocus();
            return;
        }

        // CREAR OBJETO
        try {
            double precio = Double.parseDouble(precioStr);

            Objeto nuevoObjeto = new Objeto(
                    null, // ID nulo, el servidor lo asigna
                    currentUserId, // ID del usuario autenticado
                    nombre,
                    precio,
                    "DISPONIBLE", // Estado inicial
                    categoria,
                    descripcion
            );

            // ✅ LLAMADA A RETROFIT
            enviarObjetoAServidor(nuevoObjeto);

        } catch (NumberFormatException e) {
            etPrecio.setError("Precio inválido");
            etPrecio.requestFocus();
        }
    }

    private void enviarObjetoAServidor(Objeto objeto) {
        // Puedes agregar aquí una ProgressBar o deshabilitar el botón de Agregar
        btnAgregar.setEnabled(false);
        btnAgregar.setText("Enviando...");

        apiService.agregarObjeto(objeto).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                btnAgregar.setEnabled(true);
                btnAgregar.setText("Agregar Objeto");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(plantillaagregar.this, "¡Objeto agregado exitosamente!", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar activity y volver a la lista (que se recargará en onResume)
                } else {
                    Toast.makeText(plantillaagregar.this, "Error al guardar el objeto: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                btnAgregar.setEnabled(true);
                btnAgregar.setText("Agregar Objeto");
                Toast.makeText(plantillaagregar.this, "Error de conexión al agregar objeto.", Toast.LENGTH_LONG).show();
            }
        });
    }
}