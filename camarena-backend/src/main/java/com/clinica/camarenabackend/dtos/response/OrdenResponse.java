package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrdenResponse {
    private UUID idOrden;
    private String nombrePaciente;
    private String codigoTicket;
    private LocalDateTime fechaEmision;
    private String estadoGeneral;
    private BigDecimal montoTotalCalculado; // La suma de los detalles para cobrarle al paciente
}
