package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.models.entities.NotificacionesEnviadas;
import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;
import com.clinica.camarenabackend.repositories.NotificacionesEnviadasRepository;
import com.clinica.camarenabackend.services.interfaces.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionesEnviadasRepository notificacionesRepository;

    @Override
    @Transactional
    public void enviarNotificacionResultadosListos(OrdenLaboratorio orden) {
        // En un entorno de producción, aquí usaríamos JavaMailSender o la API de Twilio (WhatsApp)
        System.out.println("Simulando envío de Email/WhatsApp a: " +
                orden.getPaciente().getOnombres() + " para el ticket " + orden.getOcodigoTicket());

        NotificacionesEnviadas notificacion = NotificacionesEnviadas.builder()
                .paciente(orden.getPaciente())
                .orden(orden)
                .ocanal("EMAIL_Y_WHATSAPP")
                .oestadoEnvio("ENVIADO")
                .ofechaEnvio(LocalDateTime.now())
                .build();

        notificacionesRepository.save(notificacion);
    }
}
