package com.example.roy.api;

// Importar los nuevos modelos DTO del backend
import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.models.RegisterRequest;

// Importar modelos existentes
import com.example.roy.models.Objeto;
import com.example.roy.models.Resena;
import com.example.roy.models.UpdateProfileRequest;
import com.example.roy.models.UserProfileResponse;
import com.example.roy.models.Usuario;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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


    //para el inicio funcional
    // Este es el que necesitamos para CategoryFragment
    @GET("/api/objeto/objeto")
    Call<List<Objeto>> getObjetos();   // 👈👈 EXACTAMENTE este nombre

    // UNO POR ID
    @GET("api/objeto/objeto/{id}")
    Call<Objeto> getObjetoPorId(@Path("id") int id);

    // BUSCADOR: /api/objeto/buscar?q=texto
    @GET("api/objeto/buscar")
    Call<List<Objeto>> buscarObjetos(@Query("q") String texto);

    // POR CATEGORÍA: /api/objeto/categoria?nombre=Eventos
    @GET("api/objeto/categoria")
    Call<List<Objeto>> objetosPorCategoria(@Query("nombre") String categoria);

    // RECOMENDADOS (si lo tienes)
    @GET("api/objeto/recomendados")
    Call<List<Objeto>> getRecomendados();

    // DESTACADO
    @GET("api/objeto/destacado")
    Call<Objeto> getDestacado();

    @Multipart
    @POST("Roy/api/usuario/{id}/foto")
    Call<String> subirFoto(@Path("id") int id, @Part MultipartBody.Part file);
    // ---------- RESEÑAS ----------
    @GET("api/resenas/objeto/{objetoId}")
    Call<List<Resena>> getResenasPorObjeto(@Path("objetoId") int objetoId);

    @POST("api/resenas")
    Call<Resena> crearResena(@Body Resena resena, @Header("Authorization") String token);

    @DELETE("api/resenas/{resenaId}")
    Call<Void> eliminarResena(@Path("resenaId") int resenaId, @Header("Authorization") String token);

    // ---------- PERFIL (Roy/api/usuario/...) ----------
    @GET("Roy/api/usuario/{id}")
    Call<UserProfileResponse> getPerfil(
            @Path("id") int userId,
            @Header("Authorization") String token
    );

    @PUT("Roy/api/usuario/{id}")
    Call<UserProfileResponse> actualizarPerfil(
            @Path("id") int userId,
            @Body UpdateProfileRequest request,
            @Header("Authorization") String token
    );

}