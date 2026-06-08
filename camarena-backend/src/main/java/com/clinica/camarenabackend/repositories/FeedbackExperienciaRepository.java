package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.FeedbackExperiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackExperienciaRepository extends JpaRepository<FeedbackExperiencia, UUID> {
}
