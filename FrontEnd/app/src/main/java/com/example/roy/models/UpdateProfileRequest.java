package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String domicilio;

    @SerializedName("zona")
    private String zona;

    @SerializedName("password")
    private String password; // Solo si quiere cambiarla

    public UpdateProfileRequest() {}

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}