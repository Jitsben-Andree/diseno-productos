package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TuboPendienteResponse {
    private UUID idMuestra;
    private String idOrdenCorto;
    private UUID idOrdenReal;
    private String codigoBarras;
    private String nombreExamen;
    private String paciente;
    private String prioridad;
    private UUID idDetalleOrden; // Para saber a qué examen pertenecen los valores a ingresar
}