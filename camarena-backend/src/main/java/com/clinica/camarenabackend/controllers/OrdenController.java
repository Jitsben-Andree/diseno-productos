package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.OrdenRequest;
import com.clinica.camarenabackend.dtos.request.PagoRequest;
import com.clinica.camarenabackend.dtos.response.OrdenResponse;
import com.clinica.camarenabackend.services.interfaces.OrdenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // 1. Crear la Orden ("Carrito de compras")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<OrdenResponse> crearOrden(@Valid @RequestBody OrdenRequest request) {
        OrdenResponse response = ordenService.crearOrden(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Registrar el Pago de la Orden y emitir comprobante
    @PostMapping("/{idOrden}/pagos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<String> registrarPago(
            @PathVariable UUID idOrden,
            @Valid @RequestBody PagoRequest request) {

        ordenService.registrarPago(idOrden, request);
        return ResponseEntity.ok("Pago registrado correctamente. La orden ha pasado a Tópico.");
    }

    // 3. Este es el endpoint exacto que Angular estaba buscando y no encontraba
    @GetMapping("/pendientes-topico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('BIOLOGO')")
    public ResponseEntity<List<OrdenResponse>> listarPendientesTopico() {
        List<OrdenResponse> lista = ordenService.listarPendientesTopico();
        return ResponseEntity.ok(lista);
    }

    // =======================================================================
    // NUEVOS ENDPOINTS: HISTORIAL Y ANULACIONES
    // =======================================================================

    // 4. Endpoint para el Historial (Soluciona el Error 403 en Angular)
    @GetMapping("/historial")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<List<OrdenResponse>> buscarHistorial(@RequestParam("buscar") String buscar) {
        List<OrdenResponse> historial = ordenService.buscarHistorial(buscar);
        return ResponseEntity.ok(historial);
    }

    // 5. Endpoint para Anular Orden y justificar Extorno
    @PostMapping("/{idOrden}/anular")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<String> anularOrden(
            @PathVariable UUID idOrden,
            @RequestBody Map<String, String> body) {

        String motivo = body.get("motivo");
        ordenService.anularOrden(idOrden, motivo);
        return ResponseEntity.ok("Orden anulada correctamente. Operación financiera extornada.");
    }
}