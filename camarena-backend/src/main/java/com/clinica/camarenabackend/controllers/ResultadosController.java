package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.IngresarResultadoRequest;
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ResultadoResponse;
import com.clinica.camarenabackend.dtos.response.TuboPendienteResponse;
import com.clinica.camarenabackend.security.models.CustomUserDetails;
import com.clinica.camarenabackend.services.interfaces.ResultadosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResultadosController {

    @Autowired
    private ResultadosService resultadosService;

    // NUEVO: Trae todos los tubos que ya salieron de Tópico para pintarlos a la izquierda
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<List<TuboPendienteResponse>> listarPendientes() {
        return ResponseEntity.ok(resultadosService.listarTubosPendientes());
    }

    // ACTUALIZADO: Trae los inputs vacíos que el Biólogo debe llenar según el catálogo
    @GetMapping("/examen/{idDetalle}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<ParametroExamenResponse>> listarParametrosParaExamen(@PathVariable UUID idDetalle) {
        return ResponseEntity.ok(resultadosService.obtenerParametrosDeExamen(idDetalle));
    }

    @PostMapping("/ingresar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<ResultadoResponse> ingresarValor(@Valid @RequestBody IngresarResultadoRequest request) {
        ResultadoResponse response = resultadosService.ingresarValorAnalitico(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/aprobar/{idOrden}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<String> aprobarYFirma(
            @PathVariable UUID idOrden,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        resultadosService.aprobarYGenerarPdf(idOrden, userDetails.getUsername());
        return ResponseEntity.ok("Orden aprobada exitosamente. PDF generado y listo para el paciente.");
    }
}