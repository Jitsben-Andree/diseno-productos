package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.OrdenRequest;
import com.clinica.camarenabackend.dtos.request.PagoRequest;
import com.clinica.camarenabackend.dtos.response.OrdenResponse;
import com.clinica.camarenabackend.services.interfaces.OrdenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // 1. Crear la Orden ("Carrito de compras")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<OrdenResponse> crearOrden(@Valid @RequestBody OrdenRequest request) {
        OrdenResponse response = ordenService.crearOrden(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Registrar el Pago de la Orden y emitir comprobante
    @PostMapping("/{idOrden}/pagos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<String> registrarPago(
            @PathVariable UUID idOrden,
            @Valid @RequestBody PagoRequest request) {

        ordenService.registrarPago(idOrden, request);
        return ResponseEntity.ok("Pago registrado correctamente. La orden ha pasado a Tópico.");
    }

    // NUEVO: Este es el endpoint exacto que Angular estaba buscando y no encontraba
    @GetMapping("/pendientes-topico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('BIOLOGO')")
    public ResponseEntity<List<OrdenResponse>> listarPendientesTopico() {
        List<OrdenResponse> lista = ordenService.listarPendientesTopico();
        return ResponseEntity.ok(lista);
    }
}