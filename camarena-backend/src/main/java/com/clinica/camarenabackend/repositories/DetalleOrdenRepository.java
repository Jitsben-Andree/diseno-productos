package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, UUID> {

    @Query("SELECT d FROM DetalleOrden d WHERE d.orden.oid_orden = :idOrden")
    List<DetalleOrden> findByOrden_Oid_orden(@Param("idOrden") UUID idOrden);
}