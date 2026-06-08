package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.response.AuditoriaResponse;

import java.util.List;

public interface AuditoriaService {

    // Método universal para registrar cualquier cambio
    void registrarAuditoria(String tablaAfectada, String accion, Object datosPrevios, Object datosNuevos);

    // Método para que el Administrador revise el historial
    List<AuditoriaResponse> listarAuditorias();
}