package com.clinica.camarenabackend.services.interfaces;


import com.clinica.camarenabackend.dtos.response.CierreCajaResponse;

import java.util.List;

public interface CajaService {
    List<CierreCajaResponse> obtenerCierreDiario(String fecha);
}