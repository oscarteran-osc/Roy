package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class Resena {

    @SerializedName("id")
    private int id;

    @SerializedName("objetoId")
    private int objetoId;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("calificacion")
    private float calificacion;

    @SerializedName("comentario")
    private String comentario;

    @SerializedName("fecha")
    private String fecha; // Formato: "dd/MM/yy" o timestamp

    // Constructor vacío
    public Resena() {
    }

    // Constructor completo
    public Resena(int id, int objetoId, int usuarioId, String nombreUsuario,
                  String avatarUrl, float calificacion, String comentario, String fecha) {
        this.id = id;
        this.objetoId = objetoId;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.avatarUrl = avatarUrl;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjetoId() {
        return objetoId;
    }

    public void setObjetoId(int objetoId) {
        this.objetoId = objetoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}