package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CitaRequest {
    @NotBlank(message = "El DNI del paciente es obligatorio")
    private String dniPaciente;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    private LocalDateTime fechaHora;
}
