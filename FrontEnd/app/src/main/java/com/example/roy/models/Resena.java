package com.example.roy.models;

public class Resena {

    private Integer idResena;
    private Integer idObjeto;       // ✅ NUEVO
    private Integer idUsAutor;
    private Integer idUsReceptor;
    private Integer calificacion;
    private String comentario;
    private String fechaResena;     // ✅ String (ISO: "2026-01-20")

    private String nombreAutor;
    private String nombreReceptor;

    public Resena() {}

    public Integer getIdResena() { return idResena; }
    public void setIdResena(Integer idResena) { this.idResena = idResena; }

    public Integer getIdObjeto() { return idObjeto; }
    public void setIdObjeto(Integer idObjeto) { this.idObjeto = idObjeto; }

    public Integer getIdUsAutor() { return idUsAutor; }
    public void setIdUsAutor(Integer idUsAutor) { this.idUsAutor = idUsAutor; }

    public Integer getIdUsReceptor() { return idUsReceptor; }
    public void setIdUsReceptor(Integer idUsReceptor) { this.idUsReceptor = idUsReceptor; }

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getFechaResena() { return fechaResena; }
    public void setFechaResena(String fechaResena) { this.fechaResena = fechaResena; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }
}
