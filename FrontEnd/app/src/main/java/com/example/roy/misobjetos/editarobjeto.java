package com.example.roy.misobjetos;

import android.os.Bundle;
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

    private ImageButton btnVolver;
    private MaterialButton btnGuardarCambios, btnCancelar;
    private ProgressBar progressBar;

    private TextInputEditText etNombre, etPrecio, etDescripcion, etImagenUrl;
    private AutoCompleteTextView actvCategoria, actvEstado, actvZona;

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
        actvZona = findViewById(R.id.actvZona);
    }

    private void setupDropdowns() {
        String[] categorias = new String[]{
                "Hogar", "Herramientas", "Electrónica", "Exteriores",
                "Deportes", "Fiestas", "Música", "Cocina", "Otros"
        };

        String[] estados = new String[]{"Disponible", "No Disponible"};

        String[] zonas = new String[]{
                "CDMX",
                "Guadalajara, Jalisco",
                "Monterrey, Nuevo León",
                "Puebla, Puebla",
                "Querétaro, Querétaro",
                "Mérida, Yucatán",
                "Tijuana, Baja California",
                "Toluca, Estado de México"
        };

        actvCategoria.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categorias));
        actvEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados));
        actvZona.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zonas));

        actvCategoria.setThreshold(0);
        actvEstado.setThreshold(0);
        actvZona.setThreshold(0);

        actvCategoria.setOnClickListener(v -> actvCategoria.showDropDown());
        actvEstado.setOnClickListener(v -> actvEstado.showDropDown());
        actvZona.setOnClickListener(v -> actvZona.showDropDown());
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
                    llenarFormulario(objetoActual);
                } else {
                    Toast.makeText(editarobjeto.this, "No se pudo cargar el objeto", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                setLoading(false);
                Toast.makeText(editarobjeto.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void llenarFormulario(Objeto o) {
        etNombre.setText(safe(o.getNombreObjeto()));
        //etPrecio.setText((o.getPrecio() != null) ? String.valueOf(o.getPrecio()) : "");
        etDescripcion.setText(safe(o.getDescripcion()));
        etImagenUrl.setText(safe(o.getImagenUrl()));

        actvCategoria.setText(safe(o.getCategoria()), false);
        actvEstado.setText(safe(o.getEstado()), false);
        actvZona.setText(safe(o.getZona()), false);
    }

    private void guardarCambios() {
        String nombre = getText(etNombre);
        String precioStr = getText(etPrecio);
        String descripcion = getText(etDescripcion);
        String imagenUrl = getText(etImagenUrl);

        String categoria = actvCategoria.getText().toString().trim();
        String estado = actvEstado.getText().toString().trim();
        String zona = actvZona.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || categoria.isEmpty() || estado.isEmpty() || zona.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio < 0) throw new NumberFormatException();
        } catch (Exception e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateObjetoRequest req = new UpdateObjetoRequest();
        req.setNombreObjeto(nombre);
        req.setPrecio(precio);
        req.setCategoria(categoria);
        req.setEstado(estado);
        req.setZona(zona);
        req.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        req.setImagenUrl(imagenUrl.isEmpty() ? null : imagenUrl);

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
                    Toast.makeText(editarobjeto.this, "Error al actualizar. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                setLoading(false);
                Toast.makeText(editarobjeto.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
