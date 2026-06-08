package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.ResultadosDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResultadosDatosRepository extends JpaRepository<ResultadosDatos, UUID> {

    // Para listar todos los resultados ingresados de un examen específico (detalle)
    @Query("SELECT r FROM ResultadosDatos r WHERE r.detalleOrden.oid_detalle = :idDetalle")
    List<ResultadosDatos> findByDetalleOrden_Oid_detalle(@Param("idDetalle") UUID idDetalle);
}
