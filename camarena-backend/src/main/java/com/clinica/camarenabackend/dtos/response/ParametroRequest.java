package com.clinica.camarenabackend.dtos.response;

import lombok.Data;

@Data
public class ParametroRequest {
    private String nombre;
    private String unidad;
    private Double valorMin;
    private Double valorMax;
    private String sexoAplica;
}