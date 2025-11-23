package com.example.RoyServices.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "MENSAJE")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer idMensaje;

    @Column(name = "id_remitente", nullable = false)
    private Integer idRemitente;

    @Column(name = "id_destinatario", nullable = false)
    private Integer idDestinatario;

    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "leido")
    private Boolean leido;
}
