package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ExamenResponse {
    private Integer idExamen;
    private String codigo;
    private String descripcion;
    private String tipoTuboDefecto;
    private BigDecimal precioBase;
}