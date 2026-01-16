
package com.example.RoyServices.dto;

import java.math.BigDecimal;

public interface ObjetoConZonaProjection {
    Integer getIdObjeto();
    Integer getIdUsArrendador();
    String getNombreObjeto();
    BigDecimal getPrecio();
    String getEstado();
    String getCategoria();
    String getDescripcion();
    String getImagenUrl();
    String getZona(); // viene de Usuario
}
