package com.clinica.camarenabackend.repositories;



import com.clinica.camarenabackend.models.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    Optional<Paciente> findByOdni(String odni);

    Boolean existsByOdni(String odni);


    @Query("SELECT p FROM Paciente p WHERE p.usuario.oid_usuario = :idUsuario")
    List<Paciente> findByUsuario_Oid_usuario(@Param("idUsuario") UUID idUsuario);
}
