package com.clinica.camarenabackend.services.interfaces;



import com.clinica.camarenabackend.dtos.request.PacienteRequest;
import com.clinica.camarenabackend.dtos.response.PacienteResponse;

import java.util.List;

public interface PacienteService {
    PacienteResponse registrarPacienteFisico(PacienteRequest request);
    PacienteResponse buscarPorDni(String dni);
    List<PacienteResponse> listarPacientes();
}
