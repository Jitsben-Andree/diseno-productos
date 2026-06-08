package com.clinica.camarenabackend.services.impl;


import com.clinica.camarenabackend.dtos.request.InsumoRequest;
import com.clinica.camarenabackend.dtos.response.InsumoResponse;
import com.clinica.camarenabackend.models.entities.InventarioInsumos;
import com.clinica.camarenabackend.repositories.InventarioInsumosRepository;
import com.clinica.camarenabackend.services.interfaces.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioInsumosRepository inventarioRepository;

    @Override
    @Transactional
    public InsumoResponse registrarOAgregarStock(InsumoRequest request) {
        // Registra una nueva caja de insumos en el almacén
        InventarioInsumos nuevoInsumo = InventarioInsumos.builder()
                .ocodigoLote(request.getCodigoLote())
                .onombreInsumo(request.getNombreInsumo())
                .ostockActual(request.getStockAgregar())
                .ostockMinimo(request.getStockMinimo())
                .build();

        InventarioInsumos guardado = inventarioRepository.save(nuevoInsumo);
        return mapearAResponse(guardado);
    }

    @Override
    public List<InsumoResponse> listarInsumos() {
        return inventarioRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private InsumoResponse mapearAResponse(InventarioInsumos insumo) {
        return InsumoResponse.builder()
                .idInsumo(insumo.getOid_insumo())
                .codigoLote(insumo.getOcodigoLote())
                .nombreInsumo(insumo.getOnombreInsumo())
                .stockActual(insumo.getOstockActual())
                .alertaStockBajo(insumo.getOstockActual() <= insumo.getOstockMinimo())
                .build();
    }
}
