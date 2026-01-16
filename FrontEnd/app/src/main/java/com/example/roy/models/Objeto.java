package com.example.roy.models;

import java.util.List;

public class Objeto {

    private Integer idObjeto;
    private Integer idUsArrendador;
    private String nombreObjeto;
    private String nomArrendador;

    public String getNomArrendador() {
        return nomArrendador;
    }

    public void setNomArrendador(String nomArrendador) {
        this.nomArrendador = nomArrendador;
    }

    private double precio;
    private String estado;
    private String categoria;
    private String descripcion;
    private String imagenUrl;
    private String zona;
    private List<String> imagenes;

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public Integer getIdObjeto() { return idObjeto; }
    public void setIdObjeto(Integer idObjeto) { this.idObjeto = idObjeto; }

    public Integer getIdUsArrendador() { return idUsArrendador; }
    public void setIdUsArrendador(Integer idUsArrendador) { this.idUsArrendador = idUsArrendador; }

    public String getNombreObjeto() { return nombreObjeto; }
    public void setNombreObjeto(String nombreObjeto) { this.nombreObjeto = nombreObjeto; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}
