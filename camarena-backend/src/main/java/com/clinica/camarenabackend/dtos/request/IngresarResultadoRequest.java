package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class IngresarResultadoRequest {

    @NotNull(message = "El ID del detalle de la orden es obligatorio")
    private UUID idDetalleOrden;

    @NotNull(message = "El ID del parámetro clínico es obligatorio")
    private Integer idParametro;

    @NotNull(message = "El valor obtenido por la máquina es obligatorio")
    private BigDecimal valorObtenido;
}
