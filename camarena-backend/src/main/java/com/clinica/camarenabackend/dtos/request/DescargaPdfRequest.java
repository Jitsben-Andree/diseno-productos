package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DescargaPdfRequest {

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "El Código de Ticket es obligatorio")
    private String codigoTicket;
}