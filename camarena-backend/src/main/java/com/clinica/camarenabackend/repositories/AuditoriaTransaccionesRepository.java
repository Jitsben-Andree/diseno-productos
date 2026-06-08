package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.AuditoriaTransacciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditoriaTransaccionesRepository extends JpaRepository<AuditoriaTransacciones, UUID> {
    // Spring Data JPA ya nos da métodos como findAll() ordenados por fecha
}