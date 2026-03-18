package com.example.roy.network;

import com.example.roy.network.models.PayPalOrderResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PayPalApiService {

    @POST("api/paypal/crear-orden")
    @FormUrlEncoded
    Call<PayPalOrderResponse> crearOrden(
            @Field("total") Double total,
            @Field("moneda") String moneda,
            @Field("descripcion") String descripcion
    );

    // ⬅️ NUEVO ENDPOINT PARA CAPTURAR
    @POST("api/paypal/capturar-pago")
    @FormUrlEncoded
    Call<Map<String, Object>> capturarPago(
            @Field("paymentId") String paymentId,
            @Field("payerId") String payerId,
            @Field("idSolicitud") int idSolicitud
    );
}