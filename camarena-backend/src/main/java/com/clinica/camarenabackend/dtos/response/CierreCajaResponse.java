package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CierreCajaResponse {
    private String ticket;
    private String paciente;
    private String dni;
    private String fecha;
    private String metodoPago;
    private BigDecimal monto;
    private String cajero;
}