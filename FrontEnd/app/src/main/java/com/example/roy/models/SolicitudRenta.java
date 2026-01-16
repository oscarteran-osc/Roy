package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class SolicitudRenta {

    @SerializedName("idSolicitud")
    private Integer idSolicitud;

    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("fechaFin")
    private String fechaFin;

    @SerializedName("estado")
    private String estado;

    @SerializedName("idObjeto")
    private Integer idObjeto;

    @SerializedName("idUsArrendador")
    private Integer idUsArrendador;

    @SerializedName("idUsArrendatario")
    private Integer idUsArrendatario;

    @SerializedName("diasRenta")
    private Integer diasRenta;

    // Constructor vacío
    public SolicitudRenta() {}

    // Getters y Setters
    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getIdObjeto() { return idObjeto; }
    public void setIdObjeto(Integer idObjeto) { this.idObjeto = idObjeto; }

    public Integer getIdUsArrendador() { return idUsArrendador; }
    public void setIdUsArrendador(Integer idUsArrendador) { this.idUsArrendador = idUsArrendador; }

    public Integer getIdUsArrendatario() { return idUsArrendatario; }
    public void setIdUsArrendatario(Integer idUsArrendatario) { this.idUsArrendatario = idUsArrendatario; }

    public Integer getDiasRenta() { return diasRenta; }
    public void setDiasRenta(Integer diasRenta) { this.diasRenta = diasRenta; }

    // ✅ Método helper para calcular monto
    public double getMonto() {
        // Por ahora retorna un valor fijo, luego lo puedes mejorar
        return 350.0 * (diasRenta != null ? diasRenta : 1);
    }
}