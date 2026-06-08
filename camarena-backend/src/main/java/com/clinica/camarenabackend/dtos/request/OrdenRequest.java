package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrdenRequest {

    @NotBlank(message = "El DNI del paciente es obligatorio")
    private String dniPaciente;

    @NotEmpty(message = "La orden debe contener al menos un examen")
    private List<Integer> idsExamenes; // Los IDs de los exámenes del catálogo que el paciente solicitó
}