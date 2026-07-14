package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest; // <-- ¡Faltaba esta línea!
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.ExamenResponse;
import com.clinica.camarenabackend.dtos.response.ParametroResponse;

import java.util.List;

public interface CatalogoService {

    ExamenResponse crearExamen(ExamenRequest request);

    List<ExamenResponse> listarExamenes();

    // ==========================================
    // PARÁMETROS
    // ==========================================
    List<ParametroResponse> listarParametrosDeExamen(Integer idExamen);

    void agregarParametroAExamen(Integer idExamen, ParametroRequest request);

    void actualizarParametro(Integer idParametro, ParametroRequest request);

    void eliminarParametro(Integer idParametro);

    // ==========================================
    // RANGOS
    // ==========================================
    void agregarRangoAParametro(Integer idParametro, RangoRequest request);
}