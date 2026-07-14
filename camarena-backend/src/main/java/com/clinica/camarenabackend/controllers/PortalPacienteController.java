package com.clinica.camarenabackend.controllers;


import com.clinica.camarenabackend.dtos.request.DescargaPdfRequest;
import com.clinica.camarenabackend.dtos.request.FeedbackRequest;
import com.clinica.camarenabackend.dtos.response.PortalDashboardResponse;
import com.clinica.camarenabackend.services.interfaces.PortalPacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortalPacienteController {

    @Autowired
    private PortalPacienteService portalService;

    // 1. Descarga Fricción Cero (Sin Token, protegido por cruce DNI + Ticket)
    @PostMapping("/descargar-pdf")
    public ResponseEntity<String> descargarResultados(@Valid @RequestBody DescargaPdfRequest request) {
        String urlPdf = portalService.obtenerUrlPdfFriccionCero(request);
        return ResponseEntity.ok(urlPdf);
    }

    // 2. Encuesta de Satisfacción (Kaizen)
    @PostMapping("/feedback")
    public ResponseEntity<String> enviarFeedback(@Valid @RequestBody FeedbackRequest request) {
        portalService.registrarFeedback(request);
        return ResponseEntity.ok("¡Gracias por ayudarnos a mejorar! Hemos recibido tu calificación.");
    }
    @GetMapping("/dashboard/{dni}/{ticket}")
    public ResponseEntity<PortalDashboardResponse> obtenerDashboard(
            @PathVariable String dni,
            @PathVariable String ticket) {

        PortalDashboardResponse response = portalService.obtenerDatosDashboard(dni, ticket);
        return ResponseEntity.ok(response);
    }
}
