package com.example.roy.misobjetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.example.roy.utils.ImageUploadHelper;
import com.example.roy.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class plantillaagregar extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etNombre, etPrecio, etDescripcion;
    private Spinner spinnerCategoria;
    private MaterialButton btnAgregar, btnCancelar;
    private ImageView mainImg, mini1, mini2, mini3;

    private ApiService apiService;
    private SessionManager sessionManager;
    private int currentUserId;

    // URLs de las 3 imágenes
    private String imagen1Url = null; // Principal
    private String imagen2Url = null;
    private String imagen3Url = null;

    private int currentImageIndex = -1; // 0=mini1, 1=mini2, 2=mini3

    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ImageUploadHelper imageUploadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantillaagregar);

        inicializarVistas();
        configurarSpinner();
        configurarImagePicker();
        configurarCamara();
        configurarListeners();

        apiService = RetrofitClient.getClient().create(ApiService.class);
        imageUploadHelper = new ImageUploadHelper(this);

        // ✅ USAR SessionManager
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void configurarImagePicker() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        procesarImagen(uri);
                    }
                }
        );
    }

    private void configurarCamara() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        procesarBitmapDirecto(bitmap);
                    }
                }
        );
    }

    private void configurarListeners() {
        btnAgregar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        // Al hacer clic en miniaturas, mostrar opciones de imagen
        mini1.setOnClickListener(v -> mostrarOpcionesImagen(0));
        mini2.setOnClickListener(v -> mostrarOpcionesImagen(1));
        mini3.setOnClickListener(v -> mostrarOpcionesImagen(2));

        // Al hacer clic en la imagen principal, cambiar entre las 3 miniaturas
        mainImg.setOnClickListener(v -> {
            // No hacer nada, solo mostrar
        });
    }

    private void mostrarOpcionesImagen(int imageIndex) {
        currentImageIndex = imageIndex;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_seleccionar_imagen, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnGaleria = dialogView.findViewById(R.id.btn_galeria);
        Button btnCamara = dialogView.findViewById(R.id.btn_camara);
        Button btnURL = dialogView.findViewById(R.id.btn_url);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar_dialog);

        btnGaleria.setOnClickListener(v -> {
            abrirGaleria();
            dialog.dismiss();
        });

        btnCamara.setOnClickListener(v -> {
            abrirCamara();
            dialog.dismiss();
        });

        btnURL.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoURL();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void abrirGaleria() {
        photoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void abrirCamara() {
        cameraLauncher.launch(null);
    }

    private void mostrarDialogoURL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresar URL de imagen");

        final EditText input = new EditText(this);
        input.setHint("https://ejemplo.com/imagen.jpg");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("Cargar", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                cargarImagenDesdeURL(url);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void cargarImagenDesdeURL(String url) {
        ImageView targetMiniatura;

        switch (currentImageIndex) {
            case 0:
                targetMiniatura = mini1;
                imagen1Url = url;
                Toast.makeText(this, "✅ Imagen 1 (Principal) cargada", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                targetMiniatura = mini2;
                imagen2Url = url;
                Toast.makeText(this, "✅ Imagen 2 cargada", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                targetMiniatura = mini3;
                imagen3Url = url;
                Toast.makeText(this, "✅ Imagen 3 cargada", Toast.LENGTH_SHORT).show();
                break;
            default:
                return;
        }

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.mas)
                .error(R.drawable.mas)
                .into(targetMiniatura);

        // Si es la primera imagen, actualizar también la principal
        if (currentImageIndex == 0) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.imagenolonger)
                    .error(R.drawable.imagenolonger)
                    .into(mainImg);
        }
    }

    private void procesarImagen(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, 800, 800);

            ImageView targetMiniatura;
            switch (currentImageIndex) {
                case 0:
                    targetMiniatura = mini1;
                    break;
                case 1:
                    targetMiniatura = mini2;
                    break;
                case 2:
                    targetMiniatura = mini3;
                    break;
                default:
                    return;
            }

            targetMiniatura.setImageBitmap(resizedBitmap);

            // Si es la primera imagen, actualizar también la principal
            if (currentImageIndex == 0) {
                mainImg.setImageBitmap(resizedBitmap);
            }

            Toast.makeText(this, "📤 Subiendo imagen...", Toast.LENGTH_SHORT).show();

            final int imageIndex = currentImageIndex;
            imageUploadHelper.uploadImage(imageUri, new ImageUploadHelper.OnUploadListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    runOnUiThread(() -> {
                        switch (imageIndex) {
                            case 0:
                                imagen1Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 1 (Principal) subida", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                imagen2Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 2 subida", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                imagen3Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 3 subida", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(plantillaagregar.this,
                            "❌ Error: " + error, Toast.LENGTH_LONG).show());
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarBitmapDirecto(Bitmap bitmap) {
        ImageView targetMiniatura;

        switch (currentImageIndex) {
            case 0:
                targetMiniatura = mini1;
                break;
            case 1:
                targetMiniatura = mini2;
                break;
            case 2:
                targetMiniatura = mini3;
                break;
            default:
                return;
        }

        Bitmap resized = redimensionarBitmap(bitmap, 800, 800);
        targetMiniatura.setImageBitmap(resized);

        // Si es la primera imagen, actualizar también la principal
        if (currentImageIndex == 0) {
            mainImg.setImageBitmap(resized);
        }

        Toast.makeText(this, "📤 Subiendo imagen...", Toast.LENGTH_SHORT).show();

        Uri tempUri = guardarBitmapTemporal(resized);
        if (tempUri != null) {
            final int imageIndex = currentImageIndex;
            imageUploadHelper.uploadImage(tempUri, new ImageUploadHelper.OnUploadListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    runOnUiThread(() -> {
                        switch (imageIndex) {
                            case 0:
                                imagen1Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 1 (Principal) subida", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                imagen2Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 2 subida", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                imagen3Url = imageUrl;
                                Toast.makeText(plantillaagregar.this, "✅ Imagen 3 subida", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(plantillaagregar.this,
                            "❌ Error: " + error, Toast.LENGTH_LONG).show());
                }
            });
        }
    }

    private Uri guardarBitmapTemporal(Bitmap bitmap) {
        try {
            java.io.File cacheDir = getCacheDir();
            java.io.File tempFile = new java.io.File(cacheDir, "temp_image_" + System.currentTimeMillis() + ".jpg");

            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            return null;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancelar) {
            finish();
        } else if (id == R.id.agregarobj) {
            agregarObjeto();
        }
    }

    private void agregarObjeto() {
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        // Validaciones
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Debes iniciar sesión", Toast.LENGTH_LONG).show();
            return;
        }

        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa el nombre del producto");
            etNombre.requestFocus();
            return;
        }

        if (precioStr.isEmpty()) {
            etPrecio.setError("Ingresa el precio");
            etPrecio.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            etDescripcion.setError("Ingresa una descripción");
            etDescripcion.requestFocus();
            return;
        }

        if ("Seleccionar categoría".equals(categoria)) {
            Toast.makeText(this, "⚠️ Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagen1Url == null) {
            Toast.makeText(this, "⚠️ Debes subir al menos la imagen principal", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);

            if (precio <= 0) {
                etPrecio.setError("El precio debe ser mayor a 0");
                etPrecio.requestFocus();
                return;
            }

            // Crear objeto
            Objeto nuevoObjeto = new Objeto();
            nuevoObjeto.setIdUsArrendador(currentUserId);
            nuevoObjeto.setNombreObjeto(nombre);
            nuevoObjeto.setPrecio(precio);
            nuevoObjeto.setEstado("Disponible");
            nuevoObjeto.setCategoria(categoria);
            nuevoObjeto.setDescripcion(descripcion);
            nuevoObjeto.setImagenUrl(imagen1Url); // Imagen principal

            // Agregar imágenes adicionales
            List<String> imagenes = new ArrayList<>();
            if (imagen1Url != null) imagenes.add(imagen1Url);
            if (imagen2Url != null) imagenes.add(imagen2Url);
            if (imagen3Url != null) imagenes.add(imagen3Url);
            nuevoObjeto.setImagenes(imagenes);

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
                btnAgregar.setText("Agregar Objeto");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(plantillaagregar.this,
                            "¡Objeto agregado exitosamente! ✅", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("objeto_agregado", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(plantillaagregar.this,
                            "Error al guardar: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                btnAgregar.setEnabled(true);
                btnAgregar.setText("Agregar Objeto");
                Toast.makeText(plantillaagregar.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}