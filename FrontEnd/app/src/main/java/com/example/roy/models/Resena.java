package com.example.roy.models;

import java.time.LocalDate;

public class Resena {

    private int idResena;
    private int idUsAutor;
    private int idUsReceptor;
    private int calificacion;
    private String comentario;
    private LocalDate fechaResena;

    private String nombreAutor;
    private String nombreReceptor;

    public Resena(int idResena, int idUsAutor, int idUsReceptor, int calificacion,
                  String comentario, LocalDate fechaResena, String nombreAutor, String nombreReceptor) {
        this.idResena = idResena;
        this.idUsAutor = idUsAutor;
        this.idUsReceptor = idUsReceptor;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaResena = fechaResena;
        this.nombreAutor = nombreAutor;
        this.nombreReceptor = nombreReceptor;
    }

    public int getIdResena() {
        return idResena;
    }

    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    public int getIdUsAutor() {
        return idUsAutor;
    }

    public void setIdUsAutor(int idUsAutor) {
        this.idUsAutor = idUsAutor;
    }

    public int getIdUsReceptor() {
        return idUsReceptor;
    }

    public void setIdUsReceptor(int idUsReceptor) {
        this.idUsReceptor = idUsReceptor;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFechaResena() {
        return fechaResena;
    }

    public void setFechaResena(LocalDate fechaResena) {
        this.fechaResena = fechaResena;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }
}