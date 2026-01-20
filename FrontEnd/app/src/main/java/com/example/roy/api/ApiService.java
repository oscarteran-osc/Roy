package com.example.roy.api;

import com.example.roy.models.AuthResponse;
import com.example.roy.models.LoginRequest;
import com.example.roy.models.RegisterRequest;
import com.example.roy.models.Objeto;
import com.example.roy.models.Resena;
import com.example.roy.models.SolicitudRenta;
import com.example.roy.models.UpdateObjetoRequest;
import com.example.roy.models.UpdateProfileRequest;
import com.example.roy.models.UserProfileResponse;
import com.example.roy.network.models.PayPalOrderResponse;

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
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginCredentials);

    @POST("auth/register")
    Call<AuthResponse> registrarUsuario(@Body RegisterRequest nuevoUsuario);

    // ==================== OBJETOS ====================
    @GET("api/objeto/objeto")
    Call<List<Objeto>> getObjetos();

    @GET("api/objeto/objeto/{id}")
    Call<Objeto> getObjetoPorId(@Path("id") int id);

    @POST("api/objeto/objeto")
    Call<Objeto> agregarObjeto(@Body Objeto nuevoObjeto);

    @DELETE("api/objeto/objeto/{id}")
    Call<Void> eliminarObjeto(@Path("id") int objetoId);

    @PUT("api/objeto/objeto/{id}")
    Call<Objeto> actualizarObjeto(@Path("id") int objetoId, @Body Objeto objeto);

    @GET("api/objeto/recomendados")
    Call<List<Objeto>> getRecomendados();

    @GET("api/objeto/destacado")
    Call<Objeto> getDestacado();

    @GET("api/objeto/buscar")
    Call<List<Objeto>> buscarObjetos(@Query("q") String texto);

    @GET("api/objeto/categoria")
    Call<List<Objeto>> objetosPorCategoria(@Query("nombre") String categoria);

    // ==================== SOLICITUDES - ARRENDATARIO ====================
    /**
     * Obtiene solicitudes que EL USUARIO envió para rentar objetos
     */
    @GET("api/solicitudes/arrendatario/{idArrendatario}")
    Call<List<SolicitudRenta>> getSolicitudesArrendatario(@Path("idArrendatario") int idArrendatario);

    /**
     * Cancela una solicitud (solo PENDIENTE)
     */
    @DELETE("api/solicitudes/{idSolicitud}")
    Call<Void> cancelarSolicitud(@Path("idSolicitud") int idSolicitud);

    /**
     * Elimina una solicitud (generalmente RECHAZADA)
     */
    @DELETE("api/solicitudes/{id}")
    Call<Void> eliminarSolicitud(@Path("id") int id);

    // ==================== SOLICITUDES - ARRENDADOR ====================
    /**
     * Obtiene solicitudes que OTROS le enviaron al usuario (dueño de objetos)
     */
    @GET("api/solicitudes/arrendador/{idArrendador}")
    Call<List<SolicitudRenta>> getSolicitudesArrendador(@Path("idArrendador") int idArrendador);

    /**
     * Aprueba una solicitud recibida
     */
    @PUT("api/solicitudes/{id}/aprobar")
    Call<Void> aprobarSolicitud(@Path("id") int idSolicitud);

    /**
     * Rechaza una solicitud recibida
     */
    @PUT("api/solicitudes/{id}/rechazar")
    Call<Void> rechazarSolicitud(@Path("id") int idSolicitud);

    /**
     * Completa una solicitud
     */
    @PUT("api/solicitudes/{id}/completar")
    Call<SolicitudRenta> completarSolicitud(@Path("id") int idSolicitud);

    // ==================== PAGOS ====================
    /**
     * Confirma el pago de una solicitud aprobada
     */
    @POST("api/pagos/confirmar")
    Call<Void> confirmarPago(@Body ConfirmacionPago confirmacion);

    // ==================== RESEÑAS ====================
    @GET("api/resenas/objeto/{objetoId}")
    Call<List<Resena>> getResenasPorObjeto(@Path("objetoId") int objetoId);

    @POST("api/resenas")
    Call<Resena> crearResena(@Body Resena resena, @Header("Authorization") String token);

    @DELETE("api/resenas/{resenaId}")
    Call<Void> eliminarResena(@Path("resenaId") int resenaId, @Header("Authorization") String token);

    // ==================== PERFIL ====================
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

    // ApiService.java


    @PUT("/api/objeto/objeto/{id}")
    Call<Objeto> actualizarObjeto(@Path("id") int id, @Body UpdateObjetoRequest request);


    @Multipart
    @PUT("Roy/api/usuario/{id}/foto")
    Call<String> actualizarFotoPerfil(
            @Path("id") int userId,
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );

    @Multipart
    @POST("Roy/api/usuario/{id}/foto")
    Call<String> subirFoto(
            @Path("id") int id,
            @Part MultipartBody.Part file
    );

    // ==================== CLASE INTERNA PARA CONFIRMACIÓN DE PAGO ====================
    class ConfirmacionPago {
        private int idSolicitud;
        private String orderId;
        private String metodoPago;
        private double monto;

        public ConfirmacionPago(int idSolicitud, String orderId, String metodoPago, double monto) {
            this.idSolicitud = idSolicitud;
            this.orderId = orderId;
            this.metodoPago = metodoPago;
            this.monto = monto;
        }

        public int getIdSolicitud() { return idSolicitud; }
        public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
    }
}