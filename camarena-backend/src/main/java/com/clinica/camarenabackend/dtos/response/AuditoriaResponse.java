package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditoriaResponse {
    private UUID idAuditoria;
    private String nombreEmpleado;
    private String cargoEmpleado;
    private String tablaAfectada;
    private String accion;
    private String direccionIp;
    private LocalDateTime fechaEvento;
    private String datosPreviosJson;
    private String datosNuevosJson;
}