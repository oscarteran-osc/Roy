package com.example.roy.models;

public class SolicitudRentaRequest {
    private int idUsArrendatario;
    private int idUsArrendador;
    private int idObjeto;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private double monto;

    // Getters y Setters
    public int getIdUsArrendatario() { return idUsArrendatario; }
    public void setIdUsArrendatario(int idUsArrendatario) { this.idUsArrendatario = idUsArrendatario; }

    public int getIdUsArrendador() { return idUsArrendador; }
    public void setIdUsArrendador(int idUsArrendador) { this.idUsArrendador = idUsArrendador; }

    public int getIdObjeto() { return idObjeto; }
    public void setIdObjeto(int idObjeto) { this.idObjeto = idObjeto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}