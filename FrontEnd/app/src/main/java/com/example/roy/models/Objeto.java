package com.example.roy.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Objeto {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("precio")
    private double precio;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("disponible")
    private boolean disponible;

    @SerializedName("arrendadorId")
    private int arrendadorId;

    @SerializedName("nombreArrendador")
    private String nombreArrendador;

    @SerializedName("imagenPrincipal")
    private String imagenPrincipal;

    @SerializedName("imagenes")
    private List<String> imagenes;

    @SerializedName("calificacionPromedio")
    private float calificacionPromedio;

    @SerializedName("totalResenas")
    private int totalResenas;

    // Constructor vacío
    public Objeto() {
    }

    // Constructor completo
    public Objeto(int id, String nombre, String descripcion, double precio,
                  String categoria, boolean disponible, int arrendadorId,
                  String nombreArrendador, String imagenPrincipal,
                  List<String> imagenes, float calificacionPromedio, int totalResenas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.arrendadorId = arrendadorId;
        this.nombreArrendador = nombreArrendador;
        this.imagenPrincipal = imagenPrincipal;
        this.imagenes = imagenes;
        this.calificacionPromedio = calificacionPromedio;
        this.totalResenas = totalResenas;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getArrendadorId() {
        return arrendadorId;
    }

    public void setArrendadorId(int arrendadorId) {
        this.arrendadorId = arrendadorId;
    }

    public String getNombreArrendador() {
        return nombreArrendador;
    }

    public void setNombreArrendador(String nombreArrendador) {
        this.nombreArrendador = nombreArrendador;
    }

    public String getImagenPrincipal() {
        return imagenPrincipal;
    }

    public void setImagenPrincipal(String imagenPrincipal) {
        this.imagenPrincipal = imagenPrincipal;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public float getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(float calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public int getTotalResenas() {
        return totalResenas;
    }

    public void setTotalResenas(int totalResenas) {
        this.totalResenas = totalResenas;
    }
}