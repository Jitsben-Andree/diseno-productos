package com.clinica.camarenabackend.dtos.response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PacienteResponse {
    private UUID idPaciente;
    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String telefono;
    private Boolean validadoReniec;

    // Si tiene valor, significa que este paciente ya está vinculado a una cuenta de Portal Web
    private UUID idUsuarioGestor;
}