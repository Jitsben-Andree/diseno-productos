package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.ParametrosClinicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParametrosClinicosRepository extends JpaRepository<ParametrosClinicos, Integer> {


    @Query("SELECT p FROM ParametrosClinicos p WHERE p.examen.oid_examen = :idExamen")
    List<ParametrosClinicos> findByExamen_Oid_examen(@Param("idExamen") Integer idExamen);

}