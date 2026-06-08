package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.response.MuestraResponse;
import com.clinica.camarenabackend.services.interfaces.MuestraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/muestras")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MuestraController {

    @Autowired
    private MuestraService muestraService;

    // 1. Generar los códigos de barras al entrar al tópico
    @PostMapping("/generar/{idOrden}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('BIOLOGO')")
    public ResponseEntity<List<MuestraResponse>> generarMuestras(@PathVariable UUID idOrden) {
        List<MuestraResponse> respuestas = muestraService.generarMuestrasParaOrden(idOrden);
        return new ResponseEntity<>(respuestas, HttpStatus.CREATED);
    }

    // 2. El técnico confirma que ya sacó la sangre (y el backend descuenta el stock)
    @PutMapping("/{idMuestra}/tomada")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('BIOLOGO')") // Idealmente aquí habría un ROLE_TOPICO
    public ResponseEntity<MuestraResponse> marcarMuestraTomada(@PathVariable UUID idMuestra) {
        MuestraResponse response = muestraService.marcarComoTomada(idMuestra);
        return ResponseEntity.ok(response);
    }
}
