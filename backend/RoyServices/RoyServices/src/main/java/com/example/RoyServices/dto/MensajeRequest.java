package com.example.RoyServices.dto;

import lombok.Data;

@Data
public class MensajeRequest {
    private Integer idDestinatario;
    private Integer idSolicitud;
    private String contenido;
}

