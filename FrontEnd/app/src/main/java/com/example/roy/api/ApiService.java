package com.example.roy.api;

// Importar los nuevos modelos DTO del backend
import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.models.RegisterRequest;

// Importar modelos existentes
import com.example.roy.models.Objeto;
import com.example.roy.models.Usuario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // ✅ CORREGIR: Agregar "auth/" antes de cada endpoint
    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginCredentials);

    @POST("auth/register")  // ✅ Cambiar de "registro" a "auth/register"
    Call<AuthResponse> registrarUsuario(@Body RegisterRequest nuevoUsuario);

    // El resto permanece igual...
    @GET("objetos/arrendador/{userId}")
    Call<List<Objeto>> getObjetosPorUsuario(@Path("userId") int userId);

    @DELETE("objetos/{objetoId}")
    Call<Void> eliminarObjeto(@Path("objetoId") int objetoId);

    @POST("objetos")
    Call<Objeto> agregarObjeto(@Body Objeto nuevoObjeto);

}