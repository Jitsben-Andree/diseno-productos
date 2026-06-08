package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.ExamenInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenInsumoRepository extends JpaRepository<ExamenInsumo, Integer> {

    // Le decimos exactamente la consulta SQL (HQL) que debe ejecutar para que no intente adivinar
    @Query("SELECT e FROM ExamenInsumo e WHERE e.examen.oid_examen = :idExamen")
    List<ExamenInsumo> findByExamen_Oid_examen(@Param("idExamen") Integer idExamen);
}