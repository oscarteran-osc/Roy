package com.example.roy.misobjetos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.example.roy.models.UpdateObjetoRequest;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.AutoCompleteTextView;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class editarobjeto extends AppCompatActivity {

    private static final String TAG = "EditarObjeto";

    private ImageButton btnVolver;
    private MaterialButton btnGuardarCambios, btnCancelar;
    private ProgressBar progressBar;

    private TextInputEditText etNombre, etPrecio, etDescripcion, etImagenUrl;
    private AutoCompleteTextView actvCategoria, actvEstado;

    private ApiService apiService;
    private int objetoId = -1;
    private Objeto objetoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarobjeto);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        objetoId = getIntent().getIntExtra("objetoId", -1);

        if (objetoId == -1) {
            Toast.makeText(this, "Error: objetoId inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupDropdowns();
        setupListeners();
        cargarObjeto();
    }

    private void initViews() {
        btnVolver = findViewById(R.id.btnVolver);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);
        progressBar = findViewById(R.id.progressBar);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagenUrl = findViewById(R.id.etImagenUrl);

        actvCategoria = findViewById(R.id.actvCategoria);
        actvEstado = findViewById(R.id.actvEstado);
    }

    private void setupDropdowns() {
        String[] categorias = new String[]{
                "Tecnología",
                "Eventos",
                "Transporte",
                "Herramientas",
                "Hogar y Muebles",
                "Deportes y Aire Libre",
                "Electrodomésticos",
                "Ropa y Accesorios",
                "Juegos y Entretenimiento",
                "Mascotas"
        };

        String[] estados = new String[]{"Disponible", "No Disponible"};

        actvCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categorias));
        actvEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados));

        actvCategoria.setThreshold(0);
        actvEstado.setThreshold(0);

        actvCategoria.setOnClickListener(v -> actvCategoria.showDropDown());
        actvEstado.setOnClickListener(v -> actvEstado.showDropDown());
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarObjeto() {
        setLoading(true);

        apiService.getObjetoPorId(objetoId).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    objetoActual = response.body();
                    Log.d(TAG, "Objeto cargado: " + objetoActual.getNombreObjeto());
                    llenarFormulario(objetoActual);
                } else {
                    Toast.makeText(editarobjeto.this, "No se pudo cargar el objeto", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Error al cargar objeto", t);
                Toast.makeText(editarobjeto.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void llenarFormulario(Objeto o) {
        etNombre.setText(safe(o.getNombreObjeto()));

        if (o.getPrecio() > 0) {
            etPrecio.setText(String.valueOf(o.getPrecio()));
        } else {
            etPrecio.setText("");
        }

        etDescripcion.setText(safe(o.getDescripcion()));
        etImagenUrl.setText(safe(o.getImagenUrl()));

        actvCategoria.setText(safe(o.getCategoria()), false);
        actvEstado.setText(safe(o.getEstado()), false);
    }

    private void guardarCambios() {
        String nombre = getText(etNombre);
        String precioStr = getText(etPrecio);
        String descripcion = getText(etDescripcion);
        String imagenUrl = getText(etImagenUrl);

        String categoria = actvCategoria.getText().toString().trim();
        String estado = actvEstado.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es requerido");
            etNombre.requestFocus();
            return;
        }

        if (precioStr.isEmpty()) {
            etPrecio.setError("El precio es requerido");
            etPrecio.requestFocus();
            return;
        }

        if (categoria.isEmpty()) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (estado.isEmpty()) {
            Toast.makeText(this, "Selecciona un estado", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio < 0) {
                etPrecio.setError("El precio no puede ser negativo");
                etPrecio.requestFocus();
                return;
            }
        } catch (Exception e) {
            etPrecio.setError("Precio inválido");
            etPrecio.requestFocus();
            return;
        }

        // Crear request
        UpdateObjetoRequest req = new UpdateObjetoRequest();
        req.setNombreObjeto(nombre);
        req.setPrecio(precio);
        req.setCategoria(categoria);
        req.setEstado(estado);
        req.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        req.setImagenUrl(imagenUrl.isEmpty() ? null : imagenUrl);

        Log.d(TAG, "Enviando actualización: " + nombre + ", $" + precio);
        actualizarObjeto(req);
    }

    private void actualizarObjeto(UpdateObjetoRequest req) {
        setLoading(true);

        apiService.actualizarObjeto(objetoId, req).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                setLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(editarobjeto.this, "Objeto actualizado ✅", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code());
                    Toast.makeText(editarobjeto.this,
                            "Error al actualizar. Código: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Error de red al actualizar", t);
                Toast.makeText(editarobjeto.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardarCambios.setEnabled(!loading);
        btnCancelar.setEnabled(!loading);
        btnVolver.setEnabled(!loading);

        btnGuardarCambios.setText(loading ? "Guardando..." : "Guardar cambios");
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}