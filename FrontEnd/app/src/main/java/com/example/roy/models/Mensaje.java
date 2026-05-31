package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class Mensaje {
    @SerializedName("idMensaje")
    private Integer idMensaje;

    @SerializedName("idRemitente")
    private Integer idRemitente;

    @SerializedName("idDestinatario")
    private Integer idDestinatario;

    @SerializedName("idSolicitud")
    private Integer idSolicitud;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("fechaEnvio")
    private String fechaEnvio;

    @SerializedName("leido")
    private Boolean leido;

    public Integer getIdMensaje() { return idMensaje; }
    public Integer getIdRemitente() { return idRemitente; }
    public Integer getIdDestinatario() { return idDestinatario; }
    public Integer getIdSolicitud() { return idSolicitud; }
    public String getContenido() { return contenido; }
    public String getFechaEnvio() { return fechaEnvio; }
    public Boolean getLeido() { return leido; }
}
