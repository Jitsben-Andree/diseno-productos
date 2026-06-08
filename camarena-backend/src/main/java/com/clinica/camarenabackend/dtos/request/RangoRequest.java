package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RangoRequest {
    @NotBlank(message = "El sexo es obligatorio (M, F o A)")
    private String sexoAplica;

    @NotNull(message = "La edad mínima es obligatoria")
    private Integer edadMinAnios;

    @NotNull(message = "La edad máxima es obligatoria")
    private Integer edadMaxAnios;

    @NotNull(message = "El valor mínimo es obligatorio")
    private BigDecimal valorMin;

    @NotNull(message = "El valor máximo es obligatorio")
    private BigDecimal valorMax;
}
