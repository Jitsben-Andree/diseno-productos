package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.response.AuditoriaResponse;
import com.clinica.camarenabackend.models.entities.AuditoriaTransacciones;
import com.clinica.camarenabackend.models.entities.Empleado;
import com.clinica.camarenabackend.models.entities.Usuario;
import com.clinica.camarenabackend.repositories.AuditoriaTransaccionesRepository;
import com.clinica.camarenabackend.repositories.EmpleadoRepository;
import com.clinica.camarenabackend.repositories.UsuarioRepository;
import com.clinica.camarenabackend.security.models.CustomUserDetails;
import com.clinica.camarenabackend.services.interfaces.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaTransaccionesRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Herramienta nativa de Spring Boot para convertir Objetos a String JSON
    private final ObjectMapper objectMapper;

    public AuditoriaServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Para soportar LocalDate y LocalDateTime
    }

    @Override
    public void registrarAuditoria(String tablaAfectada, String accion, Object datosPrevios, Object datosNuevos) {
        try {
            // 1. Obtener quién es el usuario que está haciendo la petición
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Empleado empleado = empleadoRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            // 2. Obtener la IP desde donde se hace la petición
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = request.getRemoteAddr();

            // 3. Convertir los objetos a JSON (Si son null, guarda un JSON vacío "{}")
            String jsonPrevio = datosPrevios != null ? objectMapper.writeValueAsString(datosPrevios) : "{}";
            String jsonNuevo = datosNuevos != null ? objectMapper.writeValueAsString(datosNuevos) : "{}";

            // 4. Guardar en la Base de Datos
            AuditoriaTransacciones auditoria = AuditoriaTransacciones.builder()
                    .empleado(empleado)
                    .otablaAfectada(tablaAfectada)
                    .oaccion(accion.toUpperCase()) // Ej. UPDATE, DELETE
                    .odireccionIp(ip)
                    .ofechaEvento(LocalDateTime.now())
                    .datosPrevios(jsonPrevio)
                    .datosNuevos(jsonNuevo)
                    .build();

            auditoriaRepository.save(auditoria);

        } catch (Exception e) {
            System.err.println("Error crítico guardando auditoría: " + e.getMessage());
            // No lanzamos la excepción para no interrumpir el flujo principal de la clínica si la auditoría falla
        }
    }

    @Override
    public List<AuditoriaResponse> listarAuditorias() {
        return auditoriaRepository.findAll().stream()
                .map(audi -> AuditoriaResponse.builder()
                        .idAuditoria(audi.getOid_auditoria())
                        .nombreEmpleado(audi.getEmpleado().getOnombres() + " " + audi.getEmpleado().getOapellidos())
                        .cargoEmpleado(audi.getEmpleado().getCargo())
                        .tablaAfectada(audi.getOtablaAfectada())
                        .accion(audi.getOaccion())
                        .direccionIp(audi.getOdireccionIp())
                        .fechaEvento(audi.getOfechaEvento())
                        .datosPreviosJson(audi.getDatosPrevios())
                        .datosNuevosJson(audi.getDatosNuevos())
                        .build())
                .collect(Collectors.toList());
    }
}
