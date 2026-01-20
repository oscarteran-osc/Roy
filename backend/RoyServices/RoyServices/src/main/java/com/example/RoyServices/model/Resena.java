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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Integer idResena;

    @Column(name = "id_objeto", nullable = false)   // ✅ NUEVO
    private Integer idObjeto;

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
}
