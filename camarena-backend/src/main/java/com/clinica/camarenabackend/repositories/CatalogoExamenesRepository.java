package com.clinica.camarenabackend.repositories;

import com.clinica.camarenabackend.models.entities.CatalogoExamenes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoExamenesRepository extends JpaRepository<CatalogoExamenes, Integer> {

    // Validaremos esto para no tener exámenes duplicados
    Boolean existsByOcodigo(String ocodigo);

    Optional<CatalogoExamenes> findByOcodigo(String ocodigo);
}