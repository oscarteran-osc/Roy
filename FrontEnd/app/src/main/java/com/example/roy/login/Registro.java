package com.example.roy.login;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.RegisterRequest;
import com.example.roy.utils.ImageUploadHelper;
import com.example.roy.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    private ShapeableImageView imgProfile;
    private MaterialButton btnPickPhoto, btnCrearCuenta;
    private ProgressBar progressBar;
    private TextView tvBackLogin;

    private TextInputEditText etNombres, etApellidos, etTelefono, etCorreo, etDomicilio, etPassword, etPassword2;
    private AutoCompleteTextView actvCiudad;

    private Uri selectedImageUri = null;
    private String fotoPerfilUrl = null;

    private ApiService apiService;
    private ImageUploadHelper imageUploadHelper;
    private SessionManager sessionManager; // ✅ Agregar SessionManager

    private AlertDialog dragDropDialog;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    procesarImagen(uri);
                    if (dragDropDialog != null && dragDropDialog.isShowing()) {
                        dragDropDialog.dismiss();
                    }
                }
            });

    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    procesarBitmapDirecto(bitmap);
                    if (dragDropDialog != null && dragDropDialog.isShowing()) {
                        dragDropDialog.dismiss();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        imageUploadHelper = new ImageUploadHelper(this);
        sessionManager = new SessionManager(this); // ✅ Inicializar SessionManager

        bindViews();
        setupCiudadDropdown();

        btnPickPhoto.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);
        tvBackLogin.setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        imgProfile.setOnClickListener(v -> mostrarDialogoDragDrop());

        progressBar.setVisibility(View.GONE);
    }

    private void bindViews() {
        imgProfile = findViewById(R.id.imgProfile);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        progressBar = findViewById(R.id.progressBar);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        actvCiudad = findViewById(R.id.actvCiudad);
        etDomicilio = findViewById(R.id.etDomicilio);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
    }

    private void setupCiudadDropdown() {
        String[] zonas = new String[]{
                "CDMX", "Guadalajara, Jalisco", "Monterrey, Nuevo León",
                "Puebla, Puebla", "Querétaro, Querétaro", "Mérida, Yucatán",
                "Tijuana, Baja California", "Toluca, Estado de México"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zonas);
        actvCiudad.setAdapter(adapter);
        actvCiudad.setThreshold(0);
        actvCiudad.setOnClickListener(v -> actvCiudad.showDropDown());
    }

    private void mostrarDialogoDragDrop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_drag_drop_profile, null);
        builder.setView(dialogView);

        MaterialCardView dropZone = dialogView.findViewById(R.id.drop_zone);
        ImageView iconDrop = dialogView.findViewById(R.id.icon_drop);
        Button btnExplorar = dialogView.findViewById(R.id.btn_explorar);
        Button btnTomarFoto = dialogView.findViewById(R.id.btn_tomar_foto);
        Button btnDesdeURL = dialogView.findViewById(R.id.btn_desde_url);
        Button btnQuitarFoto = dialogView.findViewById(R.id.btn_quitar_foto);
        Button btnCancelar = dialogView.findViewById(R.id.btn_cancelar_dialog);

        if (fotoPerfilUrl == null) {
            btnQuitarFoto.setVisibility(View.GONE);
        }

        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType("image/*") ||
                            event.getClipDescription().hasMimeType("application/octet-stream");

                case DragEvent.ACTION_DRAG_ENTERED:
                    dropZone.setStrokeColor(Color.parseColor("#4CAF50"));
                    dropZone.setStrokeWidth(8);
                    iconDrop.setColorFilter(Color.parseColor("#4CAF50"));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    dropZone.setStrokeColor(Color.parseColor("#E0E0E0"));
                    dropZone.setStrokeWidth(4);
                    iconDrop.clearColorFilter();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri imageUri = clipData.getItemAt(0).getUri();
                        if (imageUri != null) {
                            procesarImagen(imageUri);
                            dragDropDialog.dismiss();
                        }
                    }
                    dropZone.setStrokeColor(Color.parseColor("#E0E0E0"));
                    dropZone.setStrokeWidth(4);
                    iconDrop.clearColorFilter();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    dropZone.setStrokeColor(Color.parseColor("#E0E0E0"));
                    dropZone.setStrokeWidth(4);
                    iconDrop.clearColorFilter();
                    return true;

                default:
                    return false;
            }
        });

        btnExplorar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnTomarFoto.setOnClickListener(v -> {
            cameraLauncher.launch(null);
            dragDropDialog.dismiss();
        });
        btnDesdeURL.setOnClickListener(v -> {
            dragDropDialog.dismiss();
            mostrarDialogoURL();
        });
        btnQuitarFoto.setOnClickListener(v -> {
            quitarFoto();
            dragDropDialog.dismiss();
        });
        btnCancelar.setOnClickListener(v -> dragDropDialog.dismiss());

        dragDropDialog = builder.create();
        dragDropDialog.show();
    }

    private void mostrarDialogoURL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresar URL de imagen");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("https://ejemplo.com/foto.jpg");
        input.setPadding(50, 30, 50, 30);
        builder.setView(input);

        builder.setPositiveButton("Cargar", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                cargarImagenDesdeURL(url);
            } else {
                Toast.makeText(this, "URL vacía", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void cargarImagenDesdeURL(String url) {
        fotoPerfilUrl = url;

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.imagenolonger)
                .error(R.drawable.imagenolonger)
                .circleCrop()
                .into(imgProfile);

        Toast.makeText(this, "✅ Foto de perfil cargada", Toast.LENGTH_SHORT).show();
    }

    private void quitarFoto() {
        fotoPerfilUrl = null;
        selectedImageUri = null;
        imgProfile.setImageResource(R.drawable.imagenolonger);
        Toast.makeText(this, "Foto removida", Toast.LENGTH_SHORT).show();
    }

    private void procesarImagen(Uri imageUri) {
        try {
            selectedImageUri = imageUri;

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imgProfile.setImageBitmap(bitmap);

            subirImagenPerfil(imageUri);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarBitmapDirecto(Bitmap bitmap) {
        imgProfile.setImageBitmap(bitmap);

        Uri tempUri = guardarBitmapTemporal(bitmap);
        if (tempUri != null) {
            subirImagenPerfil(tempUri);
        }
    }

    private Uri guardarBitmapTemporal(Bitmap bitmap) {
        try {
            java.io.File cacheDir = getCacheDir();
            java.io.File tempFile = new java.io.File(cacheDir, "temp_profile_" + System.currentTimeMillis() + ".jpg");

            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnBack) {
            finish();
            return;
        }

        if (id == R.id.btnPickPhoto) {
            mostrarDialogoDragDrop();
            return;
        }

        if (id == R.id.tvBackLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (id == R.id.btnCrearCuenta) {
            realizarRegistro();
        }
    }

    private void subirImagenPerfil(Uri imageUri) {
        setLoading(true);
        Toast.makeText(this, "📤 Subiendo foto de perfil...", Toast.LENGTH_SHORT).show();

        imageUploadHelper.uploadImage(imageUri, new ImageUploadHelper.OnUploadListener() {
            @Override
            public void onSuccess(String imageUrl) {
                runOnUiThread(() -> {
                    fotoPerfilUrl = imageUrl;
                    setLoading(false);
                    Toast.makeText(Registro.this, "✅ Foto cargada correctamente",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(Registro.this, "❌ Error al subir foto: " + error,
                            Toast.LENGTH_LONG).show();
                    fotoPerfilUrl = null;
                });
            }
        });
    }

    private void realizarRegistro() {
        String nombres = getText(etNombres);
        String apellidos = getText(etApellidos);
        String telefono = getText(etTelefono);
        String correo = getText(etCorreo).trim().toLowerCase();
        String zona = actvCiudad.getText().toString().trim();
        String domicilio = getText(etDomicilio);
        String password = getText(etPassword);
        String password2 = getText(etPassword2);

        // Validaciones
        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || correo.isEmpty()
                || zona.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        String telDigits = telefono.replaceAll("\\D+", "");
        if (telDigits.length() != 10) {
            Toast.makeText(this, "El teléfono debe tener 10 dígitos (México).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.contains(" ")) {
            Toast.makeText(this, "El correo no puede tener espacios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.contains(" ")) {
            Toast.makeText(this, "La contraseña no puede tener espacios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");

        if (!hasUpper || !hasDigit || !hasSpecial) {
            Toast.makeText(this,
                    "Contraseña inválida: debe incluir 1 mayúscula, 1 número y 1 carácter especial.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);

        RegisterRequest req = new RegisterRequest();
        req.setNombre(nombres);
        req.setApellido(apellidos);
        req.setTelefono(telDigits);
        req.setCorreo(correo);
        req.setPassword(password);
        req.setZona(zona);
        req.setDomicilio(domicilio.isEmpty() ? null : domicilio);

        apiService.registrarUsuario(req).enqueue(new Callback<com.example.roy.models.AuthResponse>() {
            @Override
            public void onResponse(Call<com.example.roy.models.AuthResponse> call,
                                   Response<com.example.roy.models.AuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    com.example.roy.models.AuthResponse auth = response.body();

                    Integer userId = auth.getIdUsuario();
                    String token = auth.getToken();

                    if (userId != null && token != null) {
                        // ✅ USAR SessionManager en lugar de SharedPreferences directo
                        sessionManager.saveSession(userId, token);

                        if (fotoPerfilUrl != null) {
                            Toast.makeText(Registro.this,
                                    "¡Cuenta creada con foto! ✅", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Registro.this,
                                    "¡Cuenta creada! ✅", Toast.LENGTH_LONG).show();
                        }

                        Intent goInicio = new Intent(Registro.this, com.example.roy.Inicio.class);
                        goInicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goInicio);

                    } else {
                        Toast.makeText(Registro.this,
                                "Registro OK, pero faltan datos del servidor.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    String msg = "Error al crear cuenta. Código: " + response.code();

                    try {
                        if (response.errorBody() != null) {
                            String raw = response.errorBody().string();
                            com.example.roy.models.AuthResponse err =
                                    new com.google.gson.Gson().fromJson(raw, com.example.roy.models.AuthResponse.class);

                            if (err != null && err.getErrorMessage() != null && !err.getErrorMessage().isEmpty()) {
                                msg = err.getErrorMessage();
                            }
                        }
                    } catch (Exception ignored) { }

                    Toast.makeText(Registro.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.roy.models.AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(Registro.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCrearCuenta.setEnabled(!loading);
        btnCrearCuenta.setText(loading ? "Creando..." : "Crear cuenta");
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}