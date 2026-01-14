package com.example.roy.login;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    // UI
    private ShapeableImageView imgProfile;
    private MaterialButton btnPickPhoto, btnCrearCuenta;
    private ProgressBar progressBar;
    private TextView tvBackLogin;

    private TextInputEditText etNombres, etApellidos, etTelefono, etCorreo, etDomicilio, etPassword, etPassword2;
    private AutoCompleteTextView actvCiudad; // ESTA será tu "zona" (ciudad/región)

    // Foto seleccionada
    private Uri selectedImageUri = null;

    // API
    private ApiService apiService;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgProfile.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        bindViews();
        setupCiudadDropdown();

        btnPickPhoto.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);
        tvBackLogin.setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

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
        actvCiudad = findViewById(R.id.actvCiudad); // zona/cd/region
        etDomicilio = findViewById(R.id.etDomicilio);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
    }

    private void setupCiudadDropdown() {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zonas);
        actvCiudad.setAdapter(adapter);
        actvCiudad.setThreshold(0);
        actvCiudad.setOnClickListener(v -> actvCiudad.showDropDown());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnBack) {
            finish();
            return;
        }

        if (id == R.id.btnPickPhoto) {
            pickImageLauncher.launch("image/*");
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

    private void realizarRegistro() {
        String nombres = getText(etNombres);
        String apellidos = getText(etApellidos);
        String telefono = getText(etTelefono);
        String correo = getText(etCorreo).trim().toLowerCase();
        String zona = actvCiudad.getText().toString().trim();
        String domicilio = getText(etDomicilio);
        String password = getText(etPassword);
        String password2 = getText(etPassword2);

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

        // 1) sin espacios (ya lo tienes, lo dejo aquí)
        if (password.contains(" ")) {
            Toast.makeText(this, "La contraseña no puede tener espacios.", Toast.LENGTH_SHORT).show();
            return;
        }

// 2) longitud mínima
        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

// 3) reglas: mayúscula, número, especial
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*"); // cualquier símbolo

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
                        // ✅ Guardar sesión
                        getSharedPreferences("RoyPrefs", MODE_PRIVATE)
                                .edit()
                                .putInt("userId", userId)
                                .putString("token", token)
                                .apply();

                        // ✅ Si seleccionó foto -> subirla, si no -> entrar directo
                        if (selectedImageUri != null) {
                            setLoading(true);
                            subirFotoPerfil(userId); // aquí se maneja éxito/fallo
                        } else {
                            Toast.makeText(Registro.this, "¡Cuenta creada! ✅", Toast.LENGTH_LONG).show();
                            Intent goInicio = new Intent(Registro.this, com.example.roy.Inicio.class);
                            goInicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(goInicio);
                        }

                    } else {
                        Toast.makeText(Registro.this, "Registro OK, pero faltan datos del servidor.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Registro.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void subirFotoPerfil(int userId) {
        try {
            byte[] bytes = readBytesFromUri(selectedImageUri);

            RequestBody reqFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "profile.jpg", reqFile);

            apiService.subirFoto(userId, body).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    setLoading(false);

                    if (response.isSuccessful()) {
                        Toast.makeText(Registro.this, "Cuenta creada ✅ y foto subida ✅", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Registro.this, "Cuenta creada ✅, pero falló la foto.", Toast.LENGTH_LONG).show();
                    }

                    Intent goInicio = new Intent(Registro.this, com.example.roy.Inicio.class);
                    goInicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goInicio);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    setLoading(false);
                    Toast.makeText(Registro.this, "Cuenta creada ✅, pero error subiendo foto: " + t.getMessage(), Toast.LENGTH_LONG).show();

                    Intent goInicio = new Intent(Registro.this, com.example.roy.Inicio.class);
                    goInicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goInicio);
                }
            });

        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(this, "No se pudo leer la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();

            Intent goInicio = new Intent(Registro.this, com.example.roy.Inicio.class);
            goInicio.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goInicio);
        }
    }


    private byte[] readBytesFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new Exception("InputStream null");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        inputStream.close();

        return buffer.toByteArray();
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
