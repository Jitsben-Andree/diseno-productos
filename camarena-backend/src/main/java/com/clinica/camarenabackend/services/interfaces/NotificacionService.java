package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;

public interface NotificacionService {
    void enviarNotificacionResultadosListos(OrdenLaboratorio orden);
}
