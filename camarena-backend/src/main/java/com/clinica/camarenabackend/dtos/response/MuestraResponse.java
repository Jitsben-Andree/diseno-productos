package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class MuestraResponse {
    private UUID idMuestra;
    private String codigoBarras;
    private String estadoMuestra;

    // Datos útiles para que el técnico de enfermería sepa qué tubo usar
    private String nombreExamen;
    private String tipoTuboRequerido;
    private String nombrePaciente;
}