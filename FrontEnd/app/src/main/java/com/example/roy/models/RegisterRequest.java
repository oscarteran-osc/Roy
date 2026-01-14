package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("domicilio")
    private String domicilio;

    @SerializedName("correo")
    private String correo;

    @SerializedName("password")
    private String password;

    @SerializedName("zona")
    private String zona;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    // (Compatibilidad por si en tu código viejo usabas setDireccion)
    public String getDireccion() { return domicilio; }
    public void setDireccion(String direccion) { this.domicilio = direccion; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}
