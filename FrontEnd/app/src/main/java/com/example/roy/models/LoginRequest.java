package com.example.roy.models; // Asegúrate de que este sea tu paquete de modelos en Android

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("correo")
    private String correo;

    @SerializedName("password")
    private String password;

    // Constructor que usarás en LoginActivity
    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    // Getters (no siempre necesarios, pero son buena práctica)
    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }
}