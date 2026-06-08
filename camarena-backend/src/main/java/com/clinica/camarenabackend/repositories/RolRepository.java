package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    @Query("SELECT r FROM Rol r WHERE r.onombre_rol = :nombreRol")
    Optional<Rol> findByOnombre_rol(@Param("nombreRol") String nombreRol);

    @Query("SELECT COUNT(r) > 0 FROM Rol r WHERE r.onombre_rol = :nombreRol")
    boolean existsByOnombre_rol(@Param("nombreRol") String nombreRol);
}