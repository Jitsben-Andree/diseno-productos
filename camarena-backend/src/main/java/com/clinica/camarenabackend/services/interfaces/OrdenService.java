package com.clinica.camarenabackend.services.interfaces;


import com.clinica.camarenabackend.dtos.request.OrdenRequest;
import com.clinica.camarenabackend.dtos.request.PagoRequest;
import com.clinica.camarenabackend.dtos.response.OrdenResponse;

import java.util.List;
import java.util.UUID;

public interface OrdenService {
    OrdenResponse crearOrden(OrdenRequest request);
    void registrarPago(UUID idOrden, PagoRequest request);


    List<OrdenResponse> listarPendientesTopico();
}