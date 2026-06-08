package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.request.CitaRequest;
import com.clinica.camarenabackend.dtos.response.CitaResponse;

public interface CitaService {
    CitaResponse agendarCitaSede(CitaRequest request);
}