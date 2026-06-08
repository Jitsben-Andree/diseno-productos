package com.clinica.camarenabackend.services.interfaces;


import com.clinica.camarenabackend.dtos.response.MuestraResponse;


import java.util.List;
import java.util.UUID;

public interface MuestraService {
    List<MuestraResponse> generarMuestrasParaOrden(UUID idOrden);
    MuestraResponse marcarComoTomada(UUID idMuestra);
}
