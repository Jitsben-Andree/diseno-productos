package com.clinica.camarenabackend.services.impl;


import com.clinica.camarenabackend.dtos.request.DescargaPdfRequest;
import com.clinica.camarenabackend.dtos.request.FeedbackRequest;
import com.clinica.camarenabackend.dtos.response.PortalDashboardResponse;
import com.clinica.camarenabackend.models.entities.FeedbackExperiencia;
import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;
import com.clinica.camarenabackend.models.entities.ResultadosPdf;
import com.clinica.camarenabackend.repositories.FeedbackExperienciaRepository;
import com.clinica.camarenabackend.repositories.OrdenLaboratorioRepository;
import com.clinica.camarenabackend.repositories.ResultadosPdfRepository;
import com.clinica.camarenabackend.services.interfaces.PortalPacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PortalPacienteServiceImpl implements PortalPacienteService {

    @Autowired
    private OrdenLaboratorioRepository ordenRepository;

    @Autowired
    private ResultadosPdfRepository pdfRepository;

    @Autowired
    private FeedbackExperienciaRepository feedbackRepository;

    @Override
    public String obtenerUrlPdfFriccionCero(DescargaPdfRequest request) {
        // 1. Buscar la orden por su código de ticket
        OrdenLaboratorio orden = ordenRepository.findByOcodigoTicket(request.getCodigoTicket().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Error: Código de ticket no encontrado."));

        // 2. Validación de Seguridad (Fricción Cero, pero seguro)
        if (!orden.getPaciente().getOdni().equals(request.getDni())) {
            throw new RuntimeException("Error: El DNI no coincide con el titular de este ticket.");
        }

        // 3. Verificar que la orden esté finalizada y tenga PDF
        ResultadosPdf pdf = pdfRepository.findByOrden_Oid_orden(orden.getOid_orden())
                .orElseThrow(() -> new RuntimeException("Los resultados aún no están listos o la orden no ha sido aprobada por el biólogo."));

        return pdf.getOpdfUrl();
    }

    @Override
    @Transactional
    public void registrarFeedback(FeedbackRequest request) {
        OrdenLaboratorio orden = ordenRepository.findByOcodigoTicket(request.getCodigoTicket().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Error: Código de ticket no encontrado."));

        FeedbackExperiencia feedback = FeedbackExperiencia.builder()
                .orden(orden)
                .paciente(orden.getPaciente())
                .ocsatScore(request.getCsatScore())
                .comentariosUx(request.getComentariosUx())
                .fechaCalificacion(LocalDateTime.now())
                .build();

        feedbackRepository.save(feedback);
    }

    @Override
    public PortalDashboardResponse obtenerDatosDashboard(String dni, String ticket) {
        OrdenLaboratorio orden = ordenRepository.findByOcodigoTicket(ticket.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado en el sistema."));

        if (!orden.getPaciente().getOdni().equals(dni)) {
            throw new RuntimeException("El DNI no corresponde al titular de esta orden médica.");
        }

        return PortalDashboardResponse.builder()
                .nombrePaciente(orden.getPaciente().getOnombres()) // Para decirle "Hola, Juan"
                .estadoOrden(orden.getOestadoGeneral())            // Para saber si pintar el banner verde o naranja
                .fechaEmision(orden.getOfechaEmision())
                .codigoTicket(orden.getOcodigoTicket())
                .build();
    }
}
