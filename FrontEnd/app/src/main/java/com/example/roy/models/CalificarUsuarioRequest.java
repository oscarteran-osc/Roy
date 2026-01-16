package com.example.roy.models;

public class CalificarUsuarioRequest {
    private int userId;
    private int arrendadorId;
    private int estrellas;
    private String comentario;

    public CalificarUsuarioRequest(int userId, int arrendadorId, int estrellas, String comentario){
        this.userId = userId;
        this.arrendadorId = arrendadorId;
        this.estrellas = estrellas;
        this.comentario = comentario;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getArrendadorId() {
        return arrendadorId;
    }

    public void setArrendadorId(int arrendadorId) {
        this.arrendadorId = arrendadorId;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
