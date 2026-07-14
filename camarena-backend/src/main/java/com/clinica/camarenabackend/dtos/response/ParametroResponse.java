package com.clinica.camarenabackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParametroResponse {
    private Integer idParametro;
    private String nombre;
    private String unidad;
    private Double rangoMin;
    private Double rangoMax;
    private String sexoAplica;
}