package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdenLaboratorioRepository extends JpaRepository<OrdenLaboratorio, UUID> {
    Optional<OrdenLaboratorio> findByOcodigoTicket(String ocodigoTicket);
    List<OrdenLaboratorio> findByOestadoGeneral(String oestadoGeneral);
}
