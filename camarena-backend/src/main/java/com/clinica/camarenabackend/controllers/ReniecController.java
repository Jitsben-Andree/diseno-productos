package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.response.ReniecResponseDto;
import com.clinica.camarenabackend.services.interfaces.ReniecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Permite peticiones de Angular en desarrollo
@RestController
@RequestMapping("/api/pacientes")
public class ReniecController {

    @Autowired
    private ReniecService reniecService;

    // Este endpoint mapeará exactamente a: GET http://localhost:8080/api/pacientes/reniec/{dni}
    @GetMapping("/reniec/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {
        // Validación rápida del formato del DNI
        if (dni == null || dni.length() != 8 || !dni.matches("\\d+")) {
            return ResponseEntity.badRequest().body("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        try {
            ReniecResponseDto respuesta = reniecService.consultarDni(dni);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            // Retorna un error 404 o 500 con el mensaje amigable de la excepción
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}