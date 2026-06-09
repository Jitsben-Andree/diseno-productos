package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.response.CierreCajaResponse;
import com.clinica.camarenabackend.services.interfaces.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caja")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @GetMapping("/cierre")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<List<CierreCajaResponse>> obtenerCierreDiario(
            @RequestParam("fecha") String fecha) {

        List<CierreCajaResponse> cierre = cajaService.obtenerCierreDiario(fecha);
        return ResponseEntity.ok(cierre);
    }
}