package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.CitaRequest;
import com.clinica.camarenabackend.dtos.response.CitaResponse;
import com.clinica.camarenabackend.models.entities.Cita;
import com.clinica.camarenabackend.models.entities.Paciente;
import com.clinica.camarenabackend.repositories.CitaRepository;
import com.clinica.camarenabackend.repositories.PacienteRepository;
import com.clinica.camarenabackend.services.interfaces.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public CitaResponse agendarCitaSede(CitaRequest request) {
        Paciente paciente = pacienteRepository.findByOdni(request.getDniPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + request.getDniPaciente()));

        Cita nuevaCita = Cita.builder()
                .paciente(paciente)
                .ofechaHora(request.getFechaHora())
                .oestado("CONFIRMADA") // Fast-pass confirmado
                .build();

        Cita guardada = citaRepository.save(nuevaCita);

        return CitaResponse.builder()
                .idCita(guardada.getOid_cita())
                .nombrePaciente(paciente.getOnombres() + " " + paciente.getOapellidos())
                .fechaHora(guardada.getOfechaHora())
                .estado(guardada.getOestado())
                .build();
    }
}
