package com.example.roy.models;

import com.google.gson.annotations.SerializedName;

public class Objeto {
    @SerializedName("idObjeto")
    private Integer idObjeto;

    @SerializedName("idUsArrendador")
    private Integer idUsArrendador;

    @SerializedName("nombreObjeto")
    private String nombreObjeto;

    @SerializedName("precio")
    private Double precio;

    @SerializedName("estado")
    private String estado; // DISPONIBLE, RENTADO, MANTENIMIENTO

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("descripcion")
    private String descripcion;

    // Constructor vacío
    public Objeto() {}

    // Constructor
    public Objeto(Integer idObjeto, Integer idUsArrendador, String nombreObjeto,
                  Double precio, String estado, String categoria, String descripcion) {
        this.idObjeto = idObjeto;
        this.idUsArrendador = idUsArrendador;
        this.nombreObjeto = nombreObjeto;
        this.precio = precio;
        this.estado = estado;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getIdObjeto() { return idObjeto; }
    public void setIdObjeto(Integer idObjeto) { this.idObjeto = idObjeto; }

    public Integer getIdUsArrendador() { return idUsArrendador; }
    public void setIdUsArrendador(Integer idUsArrendador) { this.idUsArrendador = idUsArrendador; }

    public String getNombreObjeto() { return nombreObjeto; }
    public void setNombreObjeto(String nombreObjeto) { this.nombreObjeto = nombreObjeto; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
