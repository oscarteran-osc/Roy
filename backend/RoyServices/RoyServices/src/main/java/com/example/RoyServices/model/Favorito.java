package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "FAVORITO")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Integer idFavorito;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_objeto", nullable = false)
    private Integer idObjeto;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDate fechaAgregado;
}

