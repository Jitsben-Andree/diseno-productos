package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.PacienteRequest;
import com.clinica.camarenabackend.dtos.response.PacienteResponse;
import com.clinica.camarenabackend.models.entities.Paciente;
import com.clinica.camarenabackend.repositories.PacienteRepository;
import com.clinica.camarenabackend.services.interfaces.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public PacienteResponse registrarPacienteFisico(PacienteRequest request) {

        // 1. Validar que no haya DNI duplicado
        if (pacienteRepository.existsByOdni(request.getDni())) {
            throw new RuntimeException("Error: Ya existe un paciente registrado con el DNI: " + request.getDni());
        }

        // 2. Aquí a futuro se podría llamar a un servicio externo de RENIEC
        Boolean validacionReniec = false;

        // 3. Crear Entidad
        Paciente nuevoPaciente = Paciente.builder()
                .odni(request.getDni())
                .onombres(request.getNombres())
                .oapellidos(request.getApellidos())
                .ofechaNacimiento(request.getFechaNacimiento())
                .osexo(request.getSexo().toUpperCase()) // Asegurar mayúsculas para cruzar con RangosReferencia
                .telefono(request.getTelefono())
                .ovalidadoReniec(validacionReniec)
                .build();

        Paciente pacienteGuardado = pacienteRepository.save(nuevoPaciente);

        return mapearAResponse(pacienteGuardado);
    }

    @Override
    public PacienteResponse buscarPorDni(String dni) {
        Paciente paciente = pacienteRepository.findByOdni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        return mapearAResponse(paciente);
    }

    @Override
    public List<PacienteResponse> listarPacientes() {
        return pacienteRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    // Mapper auxiliar
    private PacienteResponse mapearAResponse(Paciente paciente) {
        return PacienteResponse.builder()
                .idPaciente(paciente.getOid_paciente())
                .dni(paciente.getOdni())
                .nombres(paciente.getOnombres())
                .apellidos(paciente.getOapellidos())
                .fechaNacimiento(paciente.getOfechaNacimiento())
                .sexo(paciente.getOsexo())
                .telefono(paciente.getTelefono())
                .validadoReniec(paciente.getOvalidadoReniec())
                .idUsuarioGestor(paciente.getUsuario() != null ? paciente.getUsuario().getOid_usuario() : null)
                .build();
    }
}