package com.example.roy.profile;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.UpdateProfileRequest;
import com.example.roy.models.UserProfileResponse;
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

public class EditarPerfil extends Fragment {

    // UI
    private ImageButton btnVolver;
    private ShapeableImageView imgAvatarEditar;
    private MaterialButton btnCambiarFoto, btnGuardarCambios, btnCancelar;
    private TextInputEditText etNombres, etApellidos, etTelefono, etDomicilio;
    private TextInputEditText etNuevaPassword, etConfirmarPassword;
    private AutoCompleteTextView actvCiudad;
    private ProgressBar progressBar;

    // API
    private ApiService apiService;

    // Datos de sesión
    private int userId;
    private String token;

    // Foto seleccionada
    private Uri selectedImageUri = null;

    // Datos actuales del usuario
    private UserProfileResponse perfilActual;

    // Selector de imagen
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(imgAvatarEditar);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        // Inicializar API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Vincular vistas
        initViews(view);

        // Obtener datos de sesión
        if (!obtenerDatosSesion()) {
            Toast.makeText(requireContext(), "Error: no hay sesión activa", Toast.LENGTH_SHORT).show();
            volverAtras();
            return view;
        }

        // Configurar dropdown de ciudades
        setupCiudadDropdown();

        // Configurar listeners
        setupListeners();

        // Cargar datos actuales del perfil
        cargarPerfilActual();

        return view;
    }

    private void initViews(View view) {
        btnVolver = view.findViewById(R.id.btnVolver);
        imgAvatarEditar = view.findViewById(R.id.imgAvatarEditar);
        btnCambiarFoto = view.findViewById(R.id.btnCambiarFoto);
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        etNombres = view.findViewById(R.id.etNombres);
        etApellidos = view.findViewById(R.id.etApellidos);
        etTelefono = view.findViewById(R.id.etTelefono);
        actvCiudad = view.findViewById(R.id.actvCiudad);
        etDomicilio = view.findViewById(R.id.etDomicilio);
        etNuevaPassword = view.findViewById(R.id.etNuevaPassword);
        etConfirmarPassword = view.findViewById(R.id.etConfirmarPassword);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private boolean obtenerDatosSesion() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("RoyPrefs", android.content.Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        token = prefs.getString("token", null);
        return userId != -1 && token != null;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                zonas
        );
        actvCiudad.setAdapter(adapter);
        actvCiudad.setThreshold(0);
        actvCiudad.setOnClickListener(v -> actvCiudad.showDropDown());
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> volverAtras());

        btnCancelar.setOnClickListener(v -> volverAtras());

        btnCambiarFoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarPerfilActual() {
        setLoading(true);
        String auth = "Bearer " + token;

        apiService.getPerfil(userId, auth).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    perfilActual = response.body();
                    llenarFormulario(perfilActual);
                } else {
                    Toast.makeText(requireContext(),
                            "Error al cargar perfil",
                            Toast.LENGTH_SHORT).show();
                    volverAtras();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void llenarFormulario(UserProfileResponse perfil) {
        etNombres.setText(perfil.getNombre());
        etApellidos.setText(perfil.getApellido());

        // Teléfono sin formato
        String telefono = perfil.getTelefono();
        if (telefono != null) {
            telefono = telefono.replaceAll("\\D+", ""); // Solo dígitos
        }
        etTelefono.setText(telefono);

        actvCiudad.setText(perfil.getZona(), false);
        etDomicilio.setText(perfil.getDomicilio());

        // Cargar foto
        if (perfil.getFotoUrl() != null && !perfil.getFotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(perfil.getFotoUrl())
                    .placeholder(R.drawable.profileuser)
                    .circleCrop()
                    .into(imgAvatarEditar);
        }
    }

    private void guardarCambios() {
        String nombres = getText(etNombres);
        String apellidos = getText(etApellidos);
        String telefono = getText(etTelefono);
        String zona = actvCiudad.getText().toString().trim();
        String domicilio = getText(etDomicilio);
        String nuevaPassword = getText(etNuevaPassword);
        String confirmarPassword = getText(etConfirmarPassword);

        // Validaciones
        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || zona.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Completa los campos obligatorios",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String telDigits = telefono.replaceAll("\\D+", "");
        if (telDigits.length() != 10) {
            Toast.makeText(requireContext(),
                    "El teléfono debe tener 10 dígitos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar contraseña si la está cambiando
        if (!nuevaPassword.isEmpty() || !confirmarPassword.isEmpty()) {
            if (!nuevaPassword.equals(confirmarPassword)) {
                Toast.makeText(requireContext(),
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (nuevaPassword.length() < 8) {
                Toast.makeText(requireContext(),
                        "La contraseña debe tener al menos 8 caracteres",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean hasUpper = nuevaPassword.matches(".*[A-Z].*");
            boolean hasDigit = nuevaPassword.matches(".*\\d.*");
            boolean hasSpecial = nuevaPassword.matches(".*[^A-Za-z0-9].*");

            if (!hasUpper || !hasDigit || !hasSpecial) {
                Toast.makeText(requireContext(),
                        "Contraseña debe incluir 1 mayúscula, 1 número y 1 carácter especial",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Crear request
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNombre(nombres);
        request.setApellido(apellidos);
        request.setTelefono(telDigits);
        request.setZona(zona);
        request.setDomicilio(domicilio.isEmpty() ? null : domicilio);

        // Solo incluir password si se está cambiando
        if (!nuevaPassword.isEmpty()) {
            request.setPassword(nuevaPassword);
        }

        // Enviar actualización
        actualizarPerfil(request);
    }

    private void actualizarPerfil(UpdateProfileRequest request) {
        setLoading(true);
        String auth = "Bearer " + token;

        apiService.actualizarPerfil(userId, request, auth)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call,
                                           Response<UserProfileResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            // Si hay foto nueva, subirla
                            if (selectedImageUri != null) {
                                subirFoto();
                            } else {
                                setLoading(false);
                                Toast.makeText(requireContext(),
                                        "Perfil actualizado correctamente ✅",
                                        Toast.LENGTH_LONG).show();
                                volverAtras();
                            }
                        } else {
                            setLoading(false);
                            String msg = "Error al actualizar. Código: " + response.code();

                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("EDITAR_PERFIL", "Error: " + errorBody);
                                }
                            } catch (Exception ignored) { }

                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(requireContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void subirFoto() {
        try {
            byte[] bytes = readBytesFromUri(selectedImageUri);

            RequestBody reqFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "profile.jpg", reqFile);

            String auth = "Bearer " + token;

            apiService.actualizarFotoPerfil(userId, body, auth)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            setLoading(false);

                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                        "Perfil y foto actualizados ✅",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(),
                                        "Perfil actualizado, pero error en la foto",
                                        Toast.LENGTH_LONG).show();
                            }

                            volverAtras();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            setLoading(false);
                            Toast.makeText(requireContext(),
                                    "Perfil actualizado, error subiendo foto: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            volverAtras();
                        }
                    });

        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(requireContext(),
                    "No se pudo leer la imagen: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            volverAtras();
        }
    }

    private byte[] readBytesFromUri(Uri uri) throws Exception {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
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
        btnGuardarCambios.setEnabled(!loading);
        btnCambiarFoto.setEnabled(!loading);
        btnGuardarCambios.setText(loading ? "Guardando..." : "Guardar cambios");
    }

    private void volverAtras() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}