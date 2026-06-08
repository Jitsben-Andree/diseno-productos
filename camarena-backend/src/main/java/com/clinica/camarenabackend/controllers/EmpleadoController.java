package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.EmpleadoRequest;
import com.clinica.camarenabackend.dtos.response.EmpleadoResponse;
import com.clinica.camarenabackend.services.interfaces.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // 1. Registrar un nuevo empleado (Solo el Administrador puede hacer esto)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoResponse> registrarEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        EmpleadoResponse response = empleadoService.registrarEmpleado(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Listar todos los empleados del laboratorio
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados() {
        List<EmpleadoResponse> lista = empleadoService.listarEmpleados();
        return ResponseEntity.ok(lista);
    }
}