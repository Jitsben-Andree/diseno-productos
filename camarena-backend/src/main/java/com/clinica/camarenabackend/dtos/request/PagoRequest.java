package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequest {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.1", message = "El monto debe ser mayor a 0")
    private BigDecimal montoTotal;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // Ej. EFECTIVO, YAPE, TARJETA

    private String nroComprobante; // Opcional (Ej. número de operación de Yape)
}