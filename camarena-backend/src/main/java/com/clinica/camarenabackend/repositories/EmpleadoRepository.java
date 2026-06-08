package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.Empleado;
import com.clinica.camarenabackend.models.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {

    // Para validar que no registremos al mismo empleado dos veces
    Boolean existsByOdni(String odni);

    Optional<Empleado> findByOdni(String odni);
    Optional<Empleado> findByUsuario(Usuario usuario);
}