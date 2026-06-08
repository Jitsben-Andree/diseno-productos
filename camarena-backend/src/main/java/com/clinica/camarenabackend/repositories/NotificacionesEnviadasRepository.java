package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.NotificacionesEnviadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificacionesEnviadasRepository extends JpaRepository<NotificacionesEnviadas, UUID> {
}