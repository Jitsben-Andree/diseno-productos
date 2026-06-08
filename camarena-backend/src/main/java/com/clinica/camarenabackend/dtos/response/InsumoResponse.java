package com.clinica.camarenabackend.dtos.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsumoResponse {
    private Integer idInsumo;
    private String codigoLote;
    private String nombreInsumo;
    private Integer stockActual;
    private Boolean alertaStockBajo;
}
