package com.clinica.camarenabackend.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpleadoRequest {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 15, message = "El DNI debe tener entre 8 y 15 caracteres")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    // Opcional: Solo para biólogos/médicos
    private String cmpColegiatura;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo; // Ej. "Recepcionista Principal", "Biólogo Jefe"

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Debe asignar un rol al empleado")
    private String nombreRol; // Ej. "ROLE_RECEPCION", "ROLE_BIOLOGO"
}
