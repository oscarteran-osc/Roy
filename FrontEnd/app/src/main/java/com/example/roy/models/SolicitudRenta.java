package com.example.roy.models;

public class SolicitudRenta {
    private int idSolicitud;
    private int idObjeto;
    private int idUsArrendador;
    private int idUsArrendatario;
    private String fechaInicio;
    private String fechaFin;
    private String estado; // PENDIENTE, APROBADA, RECHAZADA
    private double monto;
    private String nombreObjeto;
    private String imagenObjeto;
    private String nombreArrendatario;
    private String nombreArrendador;
    private double precioObjeto;

    // Constructor vacío
    public SolicitudRenta(int i, int i1, int i2, String date, String s, String rechazada) {
    }

    // Constructor completo
    public SolicitudRenta(int idSolicitud, int idObjeto, int idUsArrendador,
                          int idUsArrendatario, String fechaInicio, String fechaFin,
                          String estado, double monto) {
        this.idSolicitud = idSolicitud;
        this.idObjeto = idObjeto;
        this.idUsArrendador = idUsArrendador;
        this.idUsArrendatario = idUsArrendatario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.monto = monto;

    }

    // Getters
    public int getIdSolicitud() {
        return idSolicitud;
    }

    public int getIdObjeto() {
        return idObjeto;
    }

    public int getIdUsArrendador() {
        return idUsArrendador;
    }

    public int getIdUsArrendatario() {
        return idUsArrendatario;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public double getMonto() {
        return monto;
    }

    // Setters
    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    public void setIdUsArrendador(int idUsArrendador) {
        this.idUsArrendador = idUsArrendador;
    }

    public void setIdUsArrendatario(int idUsArrendatario) {
        this.idUsArrendatario = idUsArrendatario;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getNombreObjeto() { return nombreObjeto; }
    public void setNombreObjeto(String nombreObjeto) { this.nombreObjeto = nombreObjeto; }

    public String getImagenObjeto() { return imagenObjeto; }
    public void setImagenObjeto(String imagenObjeto) { this.imagenObjeto = imagenObjeto; }

    public String getNombreArrendatario() { return nombreArrendatario; }
    public void setNombreArrendatario(String nombreArrendatario) { this.nombreArrendatario = nombreArrendatario; }

    public String getNombreArrendador() { return nombreArrendador; }
    public void setNombreArrendador(String nombreArrendador) { this.nombreArrendador = nombreArrendador; }

    public double getPrecioObjeto() { return precioObjeto; }
    public void setPrecioObjeto(double precioObjeto) { this.precioObjeto = precioObjeto; }
}