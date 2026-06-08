package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.InsumoRequest;
import com.clinica.camarenabackend.dtos.response.InsumoResponse;
import com.clinica.camarenabackend.services.interfaces.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsumoResponse> agregarStock(@Valid @RequestBody InsumoRequest request) {
        InsumoResponse response = inventarioService.registrarOAgregarStock(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<List<InsumoResponse>> listarAlmacen() {
        return ResponseEntity.ok(inventarioService.listarInsumos());
    }
}