package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.MuestraClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MuestraClinicaRepository extends JpaRepository<MuestraClinica, UUID> {


    @Query("SELECT m FROM MuestraClinica m WHERE m.detalleOrden.orden.oid_orden = :idOrden")
    List<MuestraClinica> findByDetalleOrden_Orden_Oid_orden(@Param("idOrden") UUID idOrden);

    @Query("SELECT m FROM MuestraClinica m WHERE m.oestadoMuestra = 'TOMADA' AND m.detalleOrden.orden.oestadoGeneral != 'FINALIZADO'")
    List<MuestraClinica> findMuestrasParaBiologo();
}