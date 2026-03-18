package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("fecharegistro")
    private String fecharegistro;

    @SerializedName("password")
    private String password;

    // ✅ NUEVOS
    @SerializedName("zona")
    private String zona;

    @SerializedName("fotoUrl")
    private String fotoUrl;

    // Constructor vacío
    public Usuario() {}

    // Constructor completo (viejo)
    public Usuario(Integer idUsuario, String nombre, String apellido, String correo,
                   String telefono, String direccion, String fecharegistro, String password) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fecharegistro = fecharegistro;
        this.password = password;
    }

    // ✅ Constructor completo nuevo (con zona y fotoUrl)
    public Usuario(Integer idUsuario, String nombre, String apellido, String correo,
                   String telefono, String direccion, String fecharegistro, String password,
                   String zona, String fotoUrl) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fecharegistro = fecharegistro;
        this.password = password;
        this.zona = zona;
        this.fotoUrl = fotoUrl;
    }

    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFecharegistro() { return fecharegistro; }
    public void setFecharegistro(String fecharegistro) { this.fecharegistro = fecharegistro; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ✅ nuevos getters/setters
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
