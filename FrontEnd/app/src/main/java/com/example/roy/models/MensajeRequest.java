package com.example.roy.models;

public class MensajeRequest {
    private Integer idDestinatario;
    private Integer idSolicitud;
    private String contenido;

    public void setIdDestinatario(Integer idDestinatario) { this.idDestinatario = idDestinatario; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Integer getIdDestinatario() { return idDestinatario; }
    public Integer getIdSolicitud() { return idSolicitud; }
    public String getContenido() { return contenido; }
}
