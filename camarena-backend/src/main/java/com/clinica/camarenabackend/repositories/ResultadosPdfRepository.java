package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.ResultadosPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultadosPdfRepository extends JpaRepository<ResultadosPdf, UUID> {

    // Buscar el PDF de una orden específica (Ideal para el portal web del paciente)
   @Query("SELECT r FROM ResultadosPdf r WHERE r.orden.oid_orden = :idOrden")
    Optional<ResultadosPdf> findByOrden_Oid_orden(@Param("idOrden") UUID idOrden);
}