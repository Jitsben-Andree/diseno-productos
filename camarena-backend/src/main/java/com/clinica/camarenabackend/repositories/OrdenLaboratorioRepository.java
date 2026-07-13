package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdenLaboratorioRepository extends JpaRepository<OrdenLaboratorio, UUID> {
    Optional<OrdenLaboratorio> findByOcodigoTicket(String ocodigoTicket);
    List<OrdenLaboratorio> findByOestadoGeneral(String oestadoGeneral);
    @Query("SELECT o FROM OrdenLaboratorio o WHERE UPPER(o.ocodigoTicket) LIKE UPPER(CONCAT('%', :filtro, '%')) OR o.paciente.odni LIKE CONCAT('%', :filtro, '%') ORDER BY o.ofechaEmision DESC")
    List<OrdenLaboratorio> buscarHistorial(@org.springframework.data.repository.query.Param("filtro") String filtro);
}
