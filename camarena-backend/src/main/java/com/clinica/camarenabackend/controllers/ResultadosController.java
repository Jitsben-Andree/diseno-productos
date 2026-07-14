package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.IngresarResultadoRequest;
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ResultadoResponse;
import com.clinica.camarenabackend.dtos.response.TuboPendienteResponse;
import com.clinica.camarenabackend.services.interfaces.ResultadosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResultadosController {

    @Autowired
    private ResultadosService resultadosService;

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<TuboPendienteResponse>> listarPendientes() {
        return ResponseEntity.ok(resultadosService.listarTubosPendientes());
    }

    @GetMapping("/examen/{idDetalle}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<ParametroExamenResponse>> listarParametrosParaExamen(@PathVariable UUID idDetalle) {
        return ResponseEntity.ok(resultadosService.obtenerParametrosDeExamen(idDetalle));
    }

    @PostMapping("/ingresar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<ResultadoResponse> ingresarValor(@Valid @RequestBody IngresarResultadoRequest request) {
        ResultadoResponse response = resultadosService.ingresarValorAnalitico(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 🔥 CAMBIO AQUÍ: Ahora produce APPLICATION_PDF_VALUE y devuelve ResponseEntity<byte[]>
    @PostMapping(value = "/aprobar/{idOrden}", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<byte[]> aprobarYFirma(
            @PathVariable UUID idOrden,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. El servicio ahora debe devolver el arreglo de bytes del PDF
        byte[] pdfBytes = resultadosService.aprobarYGenerarPdf(idOrden, userDetails.getUsername());

        // 2. Configuramos las cabeceras para decirle al navegador que es un archivo PDF descargable
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "attachment" fuerza la descarga automática
        headers.setContentDispositionFormData("attachment", "Resultados_" + idOrden + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}