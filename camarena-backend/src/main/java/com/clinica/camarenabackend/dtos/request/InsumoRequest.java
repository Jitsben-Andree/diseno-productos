package com.clinica.camarenabackend.dtos.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InsumoRequest {
    private String codigoLote;

    @NotBlank(message = "El nombre del insumo es obligatorio")
    private String nombreInsumo;

    @NotNull(message = "La cantidad a agregar es obligatoria")
    private Integer stockAgregar;

    @NotNull(message = "El stock mínimo es obligatorio para las alertas")
    private Integer stockMinimo;
}
