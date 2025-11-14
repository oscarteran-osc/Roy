package com.example.RoyServices.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transaccion")
public class Transaccion {
    @Id
    @Column(name = "id_transaccion")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransaccion;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "estatus_pago")
    private String estatusPago;

    @Column(name = "id_solicitud")
    private Integer idSolicitud;
}