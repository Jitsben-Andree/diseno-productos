package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CitaResponse {
    private UUID idCita;
    private String nombrePaciente;
    private LocalDateTime fechaHora;
    private String estado;
}

