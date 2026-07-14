package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PortalDashboardResponse {
    private String nombrePaciente;
    private String estadoOrden;
    private LocalDateTime fechaEmision;
    private String codigoTicket;
}
