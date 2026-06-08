package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest;
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.ExamenResponse;
import com.clinica.camarenabackend.services.interfaces.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    // 1. Crear un Examen (Requiere permisos de Admin o Biólogo)
    @PostMapping("/examenes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<ExamenResponse> crearExamen(@Valid @RequestBody ExamenRequest request) {
        ExamenResponse response = catalogoService.crearExamen(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Listar todos los Exámenes (Puede verlo también Recepción para vender)
    @GetMapping("/examenes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<ExamenResponse>> listarExamenes() {
        List<ExamenResponse> lista = catalogoService.listarExamenes();
        return ResponseEntity.ok(lista);
    }

    // 3. Agregar un Parámetro a un Examen
    @PostMapping("/examenes/{idExamen}/parametros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<String> agregarParametro(
            @PathVariable Integer idExamen,
            @Valid @RequestBody ParametroRequest request) {

        catalogoService.agregarParametroAExamen(idExamen, request);
        return ResponseEntity.ok("Parámetro agregado exitosamente.");
    }

    // 4. Configurar el Rango Normal de un Parámetro
    @PostMapping("/parametros/{idParametro}/rangos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<String> agregarRango(
            @PathVariable Integer idParametro,
            @Valid @RequestBody RangoRequest request) {

        catalogoService.agregarRangoAParametro(idParametro, request);
        return ResponseEntity.ok("Rango clínico configurado exitosamente.");
    }
}