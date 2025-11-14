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
@Table(name = "SOLICITUD_RENTA")
public class SolicitudRenta {

    @Id
    @Column(name = "id_solicitud")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // Ej: "PENDIENTE", "APROBADA", "RECHAZADA", "COMPLETADA"

    @Column(name = "id_objeto", nullable = false)
    private Integer idObjeto;

    @Column(name = "id_us_arrendador", nullable = false)
    private Integer idUsArrendador;

    @Column(name = "id_us_arrendatario", nullable = false)
    private Integer idUsArrendatario;

    // @ManyToOne
    // @JoinColumn(name = "id_objeto", insertable = false, updatable = false)
    // private Objeto objeto;

    // @ManyToOne
    // @JoinColumn(name = "id_us_arrendador", insertable = false, updatable = false)
    // private Usuario arrendador;

    // @ManyToOne
    // @JoinColumn(name = "id_us_arrendatario", insertable = false, updatable = false)
    // private Usuario arrendatario;
}
