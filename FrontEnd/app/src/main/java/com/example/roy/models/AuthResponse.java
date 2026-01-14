package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correo")
    private String correo;

    @SerializedName("token")
    private String token;

    // Constructor vacío
    public AuthResponse() {
    }

    // Getters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getToken() {
        return token;
    }

    // Setters (opcionales pero útiles)
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // 👇 ESTE ES EL IMPORTANTE
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}