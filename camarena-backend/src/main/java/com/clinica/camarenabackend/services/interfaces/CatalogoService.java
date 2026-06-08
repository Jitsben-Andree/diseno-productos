package com.clinica.camarenabackend.services.interfaces;


import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest;
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.*;

import java.util.List;

public interface CatalogoService {
    ExamenResponse crearExamen(ExamenRequest request);
    List<ExamenResponse> listarExamenes();

    void agregarParametroAExamen(Integer idExamen, ParametroRequest request);
    void agregarRangoAParametro(Integer idParametro, RangoRequest request);
}