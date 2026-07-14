package com.clinica.camarenabackend.services.interfaces;


import com.clinica.camarenabackend.dtos.request.DescargaPdfRequest;
import com.clinica.camarenabackend.dtos.request.FeedbackRequest;
import com.clinica.camarenabackend.dtos.response.PortalDashboardResponse;

public interface PortalPacienteService {
    String obtenerUrlPdfFriccionCero(DescargaPdfRequest request);
    void registrarFeedback(FeedbackRequest request);

    PortalDashboardResponse obtenerDatosDashboard(String dni, String ticket);
}
