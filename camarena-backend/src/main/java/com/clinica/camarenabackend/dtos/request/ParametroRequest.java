package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParametroRequest {
    @NotBlank(message = "El nombre del parámetro es obligatorio")
    private String nombre;
    private String unidad;
    private BigDecimal valorMin;
    private BigDecimal valorMax;
    private String sexoAplica;
}