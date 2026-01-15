package com.example.roy.api;

import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.models.RegisterRequest;
import com.example.roy.models.Objeto;
import com.example.roy.models.SolicitudRenta;
import com.example.roy.models.Usuario;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // AUTH
    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginCredentials);

    @POST("auth/register")
    Call<AuthResponse> registrarUsuario(@Body RegisterRequest nuevoUsuario);

    // OBJETOS
    @GET("objetos/arrendador/{userId}")
    Call<List<Objeto>> getObjetosPorUsuario(@Path("userId") int userId);

    @DELETE("objetos/{objetoId}")
    Call<Void> eliminarObjeto(@Path("objetoId") int objetoId);

    @POST("objetos")
    Call<Objeto> agregarObjeto(@Body Objeto nuevoObjeto);

    @GET("/api/objeto/objeto")
    Call<List<Objeto>> getObjetos();

    @GET("api/objeto/objeto/{id}")
    Call<Objeto> getObjetoPorId(@Path("id") int id);

    @GET("api/objeto/buscar")
    Call<List<Objeto>> buscarObjetos(@Query("q") String texto);

    @GET("api/objeto/categoria")
    Call<List<Objeto>> objetosPorCategoria(@Query("nombre") String categoria);

    @GET("api/objeto/recomendados")
    Call<List<Objeto>> getRecomendados();

    @GET("api/objeto/destacado")
    Call<Objeto> getDestacado();

    // SOLICITUDES (reales según tu controller)

    @GET("api/solicitudes/arrendatario/{idArrendatario}")
    Call<List<SolicitudRenta>> getSolicitudesArrendatario(@Path("idArrendatario") int idArrendatario);

    @DELETE("api/solicitudes/{idSolicitud}")
    Call<Void> cancelarSolicitud(@Path("idSolicitud") int idSolicitud);

    // Estado (si te sirve)
    @PUT("api/solicitudes/{id}/aprobar")
    Call<SolicitudRenta> aprobarSolicitud(@Path("id") int idSolicitud);

    @DELETE("api/solicitudes/{id}")
    Call<Void> eliminarSolicitud(@Path("id") int id);


    @PUT("api/solicitudes/{id}/rechazar")
    Call<SolicitudRenta> rechazarSolicitud(@Path("id") int idSolicitud);

    @PUT("api/solicitudes/{id}/completar")
    Call<SolicitudRenta> completarSolicitud(@Path("id") int idSolicitud);





    // FOTO PERFIL
    @Multipart
    @POST("Roy/api/usuario/{id}/foto")
    Call<String> subirFoto(@Path("id") int id, @Part MultipartBody.Part file);
}