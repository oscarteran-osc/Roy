package com.example.roy.api;

import com.example.roy.models.Objeto; // Necesaria para List<Objeto>
import com.example.roy.models.Usuario; // Necesaria para Call<Usuario>

import java.util.List;

import retrofit2.Call;

// 👇 ESTAS SON LAS QUE FALTAN O ESTÁN INCOMPLETAS
import retrofit2.http.Body;
import retrofit2.http.GET; // 👈 NECESARIA para la línea 20
import retrofit2.http.POST;
import retrofit2.http.DELETE; // 👈 NECESARIA para la línea 24
import retrofit2.http.Path; // 👈 NECESARIA para las variables en las URLs (líneas 21 y 25)

public interface ApiService {
    // Asume que tu backend tiene un endpoint /login que espera un Usuario (solo con correo y password)
    // y devuelve el objeto Usuario completo si es exitoso.
    @POST("login")
    Call<Usuario> loginUser(@Body Usuario loginCredentials);

    // Aquí agregarías más endpoints como /objetos, /usuarios/{id}, etc.

    @POST("registro") // Asume que el endpoint de registro es /registro
    Call<Usuario> registrarUsuario(@Body Usuario nuevoUsuario);

    // 1. Obtener objetos por ID de usuario (GET)
    @GET("objetos/arrendador/{userId}")
    Call<List<Objeto>> getObjetosPorUsuario(@Path("userId") int userId);

    // 2. Eliminar objeto por ID (DELETE)
    @DELETE("objetos/{objetoId}")
    Call<Void> eliminarObjeto(@Path("objetoId") int objetoId);

    // 3. Agregar nuevo objeto (POST)
    @POST("objetos") // Asume el endpoint base '/objetos' para crear
    Call<Objeto> agregarObjeto(@Body Objeto nuevoObjeto);
}
