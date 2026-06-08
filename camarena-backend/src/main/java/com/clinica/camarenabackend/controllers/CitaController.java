package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.CitaRequest;
import com.clinica.camarenabackend.dtos.response.CitaResponse;
import com.clinica.camarenabackend.services.interfaces.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CitaController {

    @Autowired
    private CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponse> agendarCitaSede(@Valid @RequestBody CitaRequest request) {
        CitaResponse response = citaService.agendarCitaSede(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
