package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.RangosReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RangosReferenciaRepository extends JpaRepository<RangosReferencia, Integer> {


    @Query("SELECT r FROM RangosReferencia r WHERE r.parametro.oid_parametro = :idParametro")
    List<RangosReferencia> findByParametro_Oid_parametro(@Param("idParametro") Integer idParametro);
}