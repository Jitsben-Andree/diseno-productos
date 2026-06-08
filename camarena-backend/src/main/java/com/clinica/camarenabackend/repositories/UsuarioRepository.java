package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Spring Boot crea la consulta SQL automáticamente solo con leer el nombre del método
    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);
}