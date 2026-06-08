package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ResultadoResponse {
    private UUID idResultado;
    private String nombreParametro;
    private BigDecimal valorObtenido;
    private String unidadMedida;
    private Boolean esAnormal;

    // Le devolvemos el rango normal al frontend para que lo pinte de rojo si está mal
    private String rangoNormalReferencia;
}