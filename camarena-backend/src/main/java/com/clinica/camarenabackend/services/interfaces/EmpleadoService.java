package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.request.EmpleadoRequest;
import com.clinica.camarenabackend.dtos.response.EmpleadoResponse;

import java.util.List;

public interface EmpleadoService {
    EmpleadoResponse registrarEmpleado(EmpleadoRequest request);
    List<EmpleadoResponse> listarEmpleados();
}