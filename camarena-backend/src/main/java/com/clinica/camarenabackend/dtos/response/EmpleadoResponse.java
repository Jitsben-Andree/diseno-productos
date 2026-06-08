package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class EmpleadoResponse {
    private UUID idEmpleado;
    private String dni;
    private String nombres;
    private String apellidos;
    private String cmpColegiatura;
    private String cargo;

    // Datos de su cuenta de acceso
    private String email;
    private String rolAsignado;
    private Boolean activo;
}
