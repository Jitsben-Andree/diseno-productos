package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.request.IngresarResultadoRequest;
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ResultadoResponse;
import com.clinica.camarenabackend.dtos.response.TuboPendienteResponse;

import java.util.List;
import java.util.UUID;

public interface ResultadosService {
    // 1. Guarda el valor en la base de datos
    ResultadoResponse ingresarValorAnalitico(IngresarResultadoRequest request);

    // 2. Firma el documento y genera el PDF
    void aprobarYGenerarPdf(UUID idOrden, String emailBiologo);

    // 3. Trae los tubos para la columna izquierda (El que te estaba dando el 403)
    List<TuboPendienteResponse> listarTubosPendientes();

    // 4. Trae los parámetros vacíos (Glucosa, Colesterol, etc) para que el biólogo los llene
    List<ParametroExamenResponse> obtenerParametrosDeExamen(UUID idDetalleOrden);
}