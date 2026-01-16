package com.example.roy.api;

import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.models.RegisterRequest;
import com.example.roy.models.Objeto;
import com.example.roy.models.SolicitudRenta;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ========== AUTH ==========
    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginCredentials);

    @POST("auth/register")
    Call<AuthResponse> registrarUsuario(@Body RegisterRequest nuevoUsuario);

    // ========== OBJETOS ==========
    // ✅ OPCIÓN 1: Si tu backend tiene el endpoint con /api/objeto/
    @GET("api/objeto/arrendador/{userId}")
    Call<List<Objeto>> getObjetosPorUsuario(@Path("userId") int userId);

    // ✅ Si necesitas usar la ruta /objetos (sin /api), entonces el backend necesita ese endpoint
    // @GET("objetos/arrendador/{userId}")
    // Call<List<Objeto>> getObjetosPorUsuario(@Path("userId") int userId);

    @DELETE("api/objeto/{objetoId}")  // ✅ Cambiado para consistencia
    Call<Void> eliminarObjeto(@Path("objetoId") int objetoId);

    @POST("api/objeto")  // ✅ Cambiado para consistencia
    Call<Objeto> agregarObjeto(@Body Objeto nuevoObjeto);

    @GET("api/objeto/objeto")  // ✅ Ya estaba bien
    Call<List<Objeto>> getObjetos();

    @GET("api/objeto/objeto/{id}")  // ✅ Ya estaba bien
    Call<Objeto> getObjetoPorId(@Path("id") int id);

    @GET("api/objeto/recomendados")  // ✅ Ya estaba bien
    Call<List<Objeto>> getRecomendados();

    @GET("api/objeto/destacado")  // ✅ Ya estaba bien
    Call<Objeto> getDestacado();

    // ========== SOLICITUDES ==========
    @GET("api/solicitudes/arrendador/{idArrendador}")
    Call<List<SolicitudRenta>> getSolicitudesArrendador(@Path("idArrendador") int idArrendador);

    @GET("api/solicitudes/arrendatario/{idArrendatario}")
    Call<List<SolicitudRenta>> getSolicitudesArrendatario(@Path("idArrendatario") int idArrendatario);

    @POST("api/solicitudes")
    Call<SolicitudRenta> crearSolicitud(@Body SolicitudRenta solicitud);

    @PUT("api/solicitudes/{id}/aprobar")
    Call<SolicitudRenta> aprobarSolicitud(@Path("id") int idSolicitud);

    @PUT("api/solicitudes/{id}/rechazar")
    Call<SolicitudRenta> rechazarSolicitud(@Path("id") int idSolicitud);

    @DELETE("api/solicitudes/{id}")
    Call<Void> eliminarSolicitud(@Path("id") int id);

    // ========== FOTO ==========
    @Multipart
    @POST("Roy/api/usuario/{id}/foto")
    Call<String> subirFoto(@Path("id") int id, @Part MultipartBody.Part file);
}