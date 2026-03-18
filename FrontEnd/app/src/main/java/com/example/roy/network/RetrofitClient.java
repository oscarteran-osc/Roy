package com.example.roy.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // ⚠️ Cambia esta IP por la de tu PC
    private static final String BASE_URL = "http://192.168.0.11:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Logging para ver las peticiones HTTP
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cliente HTTP con timeouts largos
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)  // Aumentar timeout
                    .readTimeout(30, TimeUnit.SECONDS)     // Aumentar timeout
                    .writeTimeout(30, TimeUnit.SECONDS)    // Aumentar timeout
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static PayPalApiService getPayPalService() {
        return getClient().create(PayPalApiService.class);
    }
}
