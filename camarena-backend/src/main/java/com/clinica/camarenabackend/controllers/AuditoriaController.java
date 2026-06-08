package com.clinica.camarenabackend.controllers;


import com.clinica.camarenabackend.dtos.response.AuditoriaResponse;
import com.clinica.camarenabackend.services.interfaces.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    // Solo el SUPER ADMIN puede ver este registro sensible
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaResponse>> listarRegistroAuditoria() {
        List<AuditoriaResponse> lista = auditoriaService.listarAuditorias();
        return ResponseEntity.ok(lista);
    }
}
