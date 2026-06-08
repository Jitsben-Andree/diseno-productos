package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParametroRequest {
    @NotBlank(message = "El nombre del parámetro es obligatorio")
    private String nombre;

    private String unidad; // Ej. mg/dL, % (Puede ser nulo en algunos casos)
}