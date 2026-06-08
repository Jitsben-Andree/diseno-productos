package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.PacienteRequest;
import com.clinica.camarenabackend.dtos.response.PacienteResponse;
import com.clinica.camarenabackend.services.interfaces.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // 1. Registrar un paciente físico en sede
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<PacienteResponse> registrarPaciente(@Valid @RequestBody PacienteRequest request) {
        PacienteResponse response = pacienteService.registrarPacienteFisico(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Búsqueda rápida por DNI (Ideal para el cajón de búsqueda en Recepción)
    @GetMapping("/buscar/{dni}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('BIOLOGO')")
    public ResponseEntity<PacienteResponse> buscarPacientePorDni(@PathVariable String dni) {
        PacienteResponse response = pacienteService.buscarPorDni(dni);
        return ResponseEntity.ok(response);
    }

    // 3. Listar todos los pacientes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<List<PacienteResponse>> listarPacientes() {
        List<PacienteResponse> lista = pacienteService.listarPacientes();
        return ResponseEntity.ok(lista);
    }
}