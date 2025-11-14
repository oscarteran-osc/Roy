package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransaccionDto {
    private Integer idTransaccion;
    private BigDecimal montoTotal;
    private LocalDate fechaPago;
    private String metodoPago;
    private String estatusPago;
    private Integer idSolicitud;
}