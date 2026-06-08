package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.request.InsumoRequest;
import com.clinica.camarenabackend.dtos.response.InsumoResponse;

import java.util.List;

public interface InventarioService {
    InsumoResponse registrarOAgregarStock(InsumoRequest request);
    List<InsumoResponse> listarInsumos();
}
