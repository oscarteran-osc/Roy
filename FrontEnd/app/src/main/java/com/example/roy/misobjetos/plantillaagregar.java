package com.example.roy.misobjetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class plantillaagregar extends AppCompatActivity implements View.OnClickListener {

    private EditText etNombre, etPrecio, etDescripcion;
    private Spinner spinnerCategoria;
    private Button btnAgregar, btnCancelar;
    private ImageView mainImg, mini1, mini2, mini3;

    private ApiService apiService;
    private int currentUserId;

    private String imagenPrincipal = null;
    private int currentImageIndex = -1; // -1: main, 0: mini1, 1: mini2, 2: mini3

    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantillaagregar);

        inicializarVistas();
        configurarSpinner();
        configurarImagePicker();
        configurarListeners();

        apiService = RetrofitClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("RoyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
    }

    private void inicializarVistas() {
        etNombre = findViewById(R.id.nomobj);
        etPrecio = findViewById(R.id.precioobj);
        etDescripcion = findViewById(R.id.descobj);
        spinnerCategoria = findViewById(R.id.catobj);
        btnAgregar = findViewById(R.id.agregarobj);
        btnCancelar = findViewById(R.id.cancelar);

        mainImg = findViewById(R.id.mainimg);
        mini1 = findViewById(R.id.mini1);
        mini2 = findViewById(R.id.mini2);
        mini3 = findViewById(R.id.mini3);
    }

    private void configurarSpinner() {
        String[] categorias = {
                "Seleccionar categoría",
                "Todo",
                "Tecnología",
                "Eventos",
                "Transporte",
                "Herramientas",
                "Hogar y Muebles",
                "Deportes y Aire Libre",
                "Electrodomésticos",
                "Ropa Y Accesorios",
                "Juegos y Entretenimiento",
                "Mascotas",
                "Camping",
                "Otros"
        };

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterSpinner);
    }

    private void configurarImagePicker() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        procesarImagen(uri);
                    } else {
                        Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void configurarListeners() {
        btnAgregar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        mainImg.setOnClickListener(v -> seleccionarImagen(-1));
        mini1.setOnClickListener(v -> seleccionarImagen(0));
        mini2.setOnClickListener(v -> seleccionarImagen(1));
        mini3.setOnClickListener(v -> seleccionarImagen(2));
    }

    private void seleccionarImagen(int imageIndex) {
        currentImageIndex = imageIndex;
        abrirGaleria();
    }

    private void abrirGaleria() {
        photoPickerLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void procesarImagen(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, 800, 800);
            String base64Image = bitmapToBase64(resizedBitmap);

            // Guardar solo la imagen principal por ahora
            if (currentImageIndex == -1) {
                imagenPrincipal = base64Image;
                mainImg.setImageBitmap(resizedBitmap);
            } else {
                // Mostrar en las miniaturas
                switch (currentImageIndex) {
                    case 0:
                        mini1.setImageBitmap(resizedBitmap);
                        break;
                    case 1:
                        mini2.setImageBitmap(resizedBitmap);
                        break;
                    case 2:
                        mini3.setImageBitmap(resizedBitmap);
                        break;
                }
            }

            Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.cancelar) {
            finish(); // ✅ Cierra la Activity y regresa al Fragment
        } else if (id == R.id.agregarobj) {
            agregarObjeto();
        }
    }

    private void agregarObjeto() {
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        // Validación de sesión
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Debes iniciar sesión para agregar objetos.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Validación de nombre
        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa un nombre");
            etNombre.requestFocus();
            return;
        }

        // Validación de precio
        if (precioStr.isEmpty()) {
            etPrecio.setError("Ingresa un precio");
            etPrecio.requestFocus();
            return;
        }

        // Validación de categoría
        if ("Seleccionar categoría".equals(categoria)) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de descripción
        if (descripcion.isEmpty()) {
            etDescripcion.setError("Agrega una descripción");
            etDescripcion.requestFocus();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);

            // Crear objeto
            Objeto nuevoObjeto = new Objeto();
            nuevoObjeto.setIdUsArrendador(currentUserId);
            nuevoObjeto.setNombreObjeto(nombre);
            nuevoObjeto.setPrecio(precio);
            nuevoObjeto.setEstado("Disponible"); // ✅ Corregido
            nuevoObjeto.setCategoria(categoria);
            nuevoObjeto.setDescripcion(descripcion);
            nuevoObjeto.setImagenUrl(imagenPrincipal);

            enviarObjetoAServidor(nuevoObjeto);

        } catch (NumberFormatException e) {
            etPrecio.setError("Precio inválido");
            etPrecio.requestFocus();
        }
    }

    private void enviarObjetoAServidor(Objeto objeto) {
        btnAgregar.setEnabled(false);
        btnAgregar.setText("Enviando...");

        apiService.agregarObjeto(objeto).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                btnAgregar.setEnabled(true);
                btnAgregar.setText("Agregar");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(plantillaagregar.this,
                            "¡Objeto agregado exitosamente!",
                            Toast.LENGTH_SHORT).show();

                    // Enviar resultado al Fragment
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("objeto_agregado", true);
                    setResult(RESULT_OK, resultIntent);

                    finish(); // ✅ Cierra Activity y regresa al Fragment MisObjetos
                } else {
                    Toast.makeText(plantillaagregar.this,
                            "Error al guardar: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                btnAgregar.setEnabled(true);
                btnAgregar.setText("Agregar");
                Toast.makeText(plantillaagregar.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}