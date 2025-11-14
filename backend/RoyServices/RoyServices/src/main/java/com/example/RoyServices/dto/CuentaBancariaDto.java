package com.example.RoyServices.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CuentaBancariaDto {
    private int  idCuentaBancaria;
    private String banco;
    private String num_tarjeta;
    private Integer idUsuario;
}
