package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest;
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.ExamenResponse;
// Asumo que tienes o crearás un DTO para enviar el parámetro al frontend:
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ParametroResponse;
import com.clinica.camarenabackend.services.interfaces.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    // 1. Crear un Examen
    @PostMapping("/examenes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<ExamenResponse> crearExamen(@Valid @RequestBody ExamenRequest request) {
        ExamenResponse response = catalogoService.crearExamen(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Listar todos los Exámenes
    @GetMapping("/examenes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<ExamenResponse>> listarExamenes() {
        List<ExamenResponse> lista = catalogoService.listarExamenes();
        return ResponseEntity.ok(lista);
    }

    // =========================================================
    // ENDPOINTS NUEVOS Y CORREGIDOS PARA EL FRONTEND
    // =========================================================

    // 3. Listar Parámetros de un Examen (Necesario para llenar la tabla del modal)
    @GetMapping("/examenes/{idExamen}/parametros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO') or hasRole('RECEPCION')")
    public ResponseEntity<List<ParametroResponse>> listarParametrosDeExamen(@PathVariable Integer idExamen) {

        List<ParametroResponse> lista = catalogoService.listarParametrosDeExamen(idExamen);
        return ResponseEntity.ok(lista); // <-- Aquí ya no debería marcar rojo
    }

    // 4. Agregar un Parámetro a un Examen (Corregido a JSON)
    @PostMapping("/examenes/{idExamen}/parametros")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<Map<String, String>> agregarParametro(
            @PathVariable Integer idExamen,
            @Valid @RequestBody ParametroRequest request) {

        catalogoService.agregarParametroAExamen(idExamen, request);
        // Devolvemos JSON: {"message": "Parámetro agregado..."}
        return ResponseEntity.ok(Map.of("message", "Parámetro agregado exitosamente."));
    }

    // 5. Editar un Parámetro (Necesario para el botón del lapicero naranja)
    @PutMapping("/parametros/{idParametro}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<Map<String, String>> actualizarParametro(
            @PathVariable Integer idParametro,
            @Valid @RequestBody ParametroRequest request) {

        // Debes crear este método en tu CatalogoService
        catalogoService.actualizarParametro(idParametro, request);
        return ResponseEntity.ok(Map.of("message", "Parámetro actualizado exitosamente."));
    }

    // 6. Eliminar un Parámetro (Necesario para el botón rojo de basura)
    @DeleteMapping("/parametros/{idParametro}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<Map<String, String>> eliminarParametro(@PathVariable Integer idParametro) {
        // Debes crear este método en tu CatalogoService
        catalogoService.eliminarParametro(idParametro);
        return ResponseEntity.ok(Map.of("message", "Parámetro eliminado exitosamente."));
    }

    // 7. Configurar Rango
    // (Ojo: Si en el frontend enviamos Rango y Parámetro en el mismo formulario,
    // lo ideal es que tu ParametroRequest incluya los rangos y guardes todo en el endpoint #4).
    @PostMapping("/parametros/{idParametro}/rangos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BIOLOGO')")
    public ResponseEntity<Map<String, String>> agregarRango(
            @PathVariable Integer idParametro,
            @Valid @RequestBody RangoRequest request) {

        catalogoService.agregarRangoAParametro(idParametro, request);
        return ResponseEntity.ok(Map.of("message", "Rango clínico configurado exitosamente."));
    }
}