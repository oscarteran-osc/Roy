package com.example.RoyServices.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "REPORTE")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    @Column(name = "id_usuario_reportante", nullable = false)
    private Integer idUsuarioReportante;

    @Column(name = "id_usuario_reportado")
    private Integer idUsuarioReportado;

    @Column(name = "id_objeto_reportado")
    private Integer idObjetoReportado;

    @Column(name = "motivo", nullable = false, length = 100)
    private String motivo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDate fechaReporte;
}

