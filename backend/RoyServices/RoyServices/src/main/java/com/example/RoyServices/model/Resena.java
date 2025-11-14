package com.example.RoyServices.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "RESENA")
public class Resena {

    @Id
    @Column(name = "id_resena")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idResena;

    @Column(name = "id_us_autor", nullable = false)
    private Integer idUsAutor;

    @Column(name = "id_us_receptor", nullable = false)
    private Integer idUsReceptor;

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_resena", nullable = false)
    private LocalDate fechaResena;

    // @ManyToOne
    // @JoinColumn(name = "id_us_autor", insertable = false, updatable = false)
    // private Usuario autor;

    // @ManyToOne
    // @JoinColumn(name = "id_us_receptor", insertable = false, updatable = false)
    // private Usuario receptor;

}
