package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ParametroExamenResponse {
    private Integer idParametro;
    private String nombre;
    private String unidad;
    private BigDecimal rangoMin;
    private BigDecimal rangoMax;
    private BigDecimal valorObtenido; // Nulo por defecto para que el Biólogo lo llene
}
